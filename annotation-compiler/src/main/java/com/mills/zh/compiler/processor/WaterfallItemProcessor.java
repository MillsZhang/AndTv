package com.mills.zh.compiler.processor;

import com.google.auto.service.AutoService;
import com.mills.zh.annotation.waterfall.WaterfallItem;
import com.mills.zh.compiler.model.Item;
import com.mills.zh.compiler.utils.Constants;
import com.mills.zh.compiler.utils.Logger;
import com.mills.zh.compiler.utils.RClassScaner;
import com.mills.zh.compiler.utils.RClassScaner.Id;
import com.mills.zh.compiler.utils.RClassScaner.QualifiedId;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.sun.source.util.Trees;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.lang.instrument.IllegalClassFormatException;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;

/**
 * Created by zhangmd on 2018/11/20.
 */

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_7)
@SupportedAnnotationTypes({Constants.ANNOTATION_TYPE_WATERFALL_ITEM})
public class WaterfallItemProcessor extends AbstractProcessor {
    private static final String TAG = ":WaterfallItem";

    private Elements elementUtils;
    private Types typeUtils;
    private Trees trees;
    private Logger logger;
    private Filer filer;

    private String module;
    private int startId;

    private RClassScaner scaner;

    private ClassName baseItem = ClassName.get(Constants.WATERFALL_COMMON_PKG, Constants.WATERFALL_BASE_ITEM_CLASS);
    private HashMap<String, Item> items;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);

        elementUtils = processingEnvironment.getElementUtils();
        typeUtils = processingEnvironment.getTypeUtils();
        logger = new Logger(processingEnvironment.getMessager());
        filer = processingEnvironment.getFiler();
        try {
            trees = Trees.instance(processingEnv);
        } catch (IllegalArgumentException ignored) {
        }

        scaner = new RClassScaner(trees, elementUtils, typeUtils);

        String startid = null;
        Map<String, String> options = processingEnv.getOptions();
        if (MapUtils.isNotEmpty(options)) {
            module = options.get("module");
            startid = options.get("startid");
        }
        if (StringUtils.isEmpty(module)) {
            module = "default";
        }
        if (StringUtils.isEmpty(startid)) {
            startId = Constants.WATERFALL_ITEM_TYPE_DEFAULT_START_ID;
        } else {
            try {
                startId = Integer.parseInt(startid);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                startId = Constants.WATERFALL_ITEM_TYPE_DEFAULT_START_ID;
            }
        }
        logger.info(module+TAG, "init");
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        logger.info(module+TAG, "process start");

        if(CollectionUtils.isNotEmpty(set)){

            scaner.scan(roundEnvironment);
            logger.info(module+TAG, "process after RClass scan");

            Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(WaterfallItem.class);

            try {
                if(CollectionUtils.isNotEmpty(elements)) {
                    //
                    for(Element element : elements){
                        if(element.getKind() != ElementKind.CLASS){
                            throw new IllegalClassFormatException("WaterfallItem: Only class can be annotated width " + Constants.ANNOTATION_TYPE_WATERFALL_TEMPLATE);
                        }
                        if(!element.getModifiers().contains(Modifier.PUBLIC)){
                            throw new IllegalClassFormatException("WaterfallItem: the class is not public!");
                        }

                        TypeMirror tm = element.asType();
                        WaterfallItem waterfallitem = element.getAnnotation(WaterfallItem.class);
                        logger.info(module+TAG, "Find WaterfallItem annotation:" + tm.toString());

                        String type = waterfallitem.type();
                        if(StringUtils.isEmpty(type)){
                            type = element.getSimpleName().toString();
                        }

                        if(items == null){
                            items = new HashMap<String, Item>();
                        }
                        if(items.containsKey(type)){
                            logger.error(module+TAG, "Find reduplicate waterfall item!");
                        } else {
                            Item item = new Item();
                            item.setTypeName(Constants.WATERFALL_ITEM_TYPE_PREFIX + type.toUpperCase());
                            item.setType(startId++);
                            item.setTypeMirror(element.asType());

                            QualifiedId qualifiedId = scaner.elementToQualifiedId(element, waterfallitem.layout());
                            Id id = scaner.getId(qualifiedId);

                            item.setLayout(id.getCode());
                            items.put(type, item);
                        }
                    }

                    if(items != null){
                        generateWaterfallItemClass();
                    }
                }
            } catch (Exception e) {
                logger.error(module+TAG, e);
            }
            return true;
        }

        return false;
    }

    private void generateWaterfallItemClass(){
        try {
            logger.info(module+TAG, "generateWaterfallItemClass");
            TypeSpec.Builder builder = TypeSpec.classBuilder(Constants.WATERFALL_ITEM_CLASS)
                    .addModifiers(PUBLIC);

            // init block template types
            Iterator<Map.Entry<String, Item>> iterator = items.entrySet().iterator();
            while (iterator != null && iterator.hasNext()){

                Map.Entry<String, Item> item = iterator.next();

                FieldSpec field = FieldSpec.builder(TypeName.INT, item.getValue().getTypeName())
                        .addModifiers(PUBLIC, STATIC, FINAL)
                        .initializer("$L", item.getValue().getType())
                        .build();

                builder.addField(field);
            }

            // base getItem method
            MethodSpec.Builder method1 = MethodSpec.methodBuilder(Constants.WATERFALL_ITEMS_METHOD_GET_ITEM)
                    .addModifiers(PRIVATE, STATIC)
                    .addParameter(ClassName.get(Constants.ANDROID_APP_PKG, Constants.ACTIVITY), "activity")
                    .addParameter(ClassName.get(Constants.ANDROID_SUPPORT_V4_APP_PKG, Constants.FRAGMENT), "fragment")
                    .addParameter(TypeName.INT, "itemType")
                    .addParameter(ClassName.get(Constants.ANDROID_VIEW_PKG, Constants.VIEWGROUP), "parent")
                    .returns(baseItem);

            CodeBlock.Builder block1 = CodeBlock.builder();
            ClassName inflater = ClassName.get(Constants.ANDROID_VIEW_PKG, Constants.LAYOUT_INFLATER);
            block1.add(
                    "$T inflater = null;\n" +
                    "if(activity != null) {\n" +
                    "  inflater = $T.from(activity);\n" +
                    "} else {\n" +
                    "  inflater = $T.from(fragment.getContext());\n" +
                    "}\n", inflater, inflater, inflater);
            block1.add("int layoutResId = -1;\n");
            block1.beginControlFlow("switch (itemType)");
            // add case
            iterator = items.entrySet().iterator();
            while (iterator != null && iterator.hasNext()){
                Map.Entry<String, Item> item = iterator.next();

                block1.add(
                        "case $L:\n" + "layoutResId = $L;\n break;\n",
                        item.getValue().getTypeName(), item.getValue().getLayout()
                );
            }
            block1.endControlFlow();

            block1.add("$T itemView = null;\n", ClassName.get(Constants.ANDROID_VIEW_PKG, Constants.VIEW));
            block1.add(
                    "if(parent == null) {\n" +
                    "  itemView = inflater.inflate(layoutResId, null);\n" +
                    "} else {\n" +
                    "  itemView = inflater.inflate(layoutResId, parent, false);\n" +
                    "}\n"
            );

            // add new item
            block1.add("$T item = null;\n", baseItem);
            block1.beginControlFlow("switch (itemType)");
            iterator = items.entrySet().iterator();
            while (iterator != null && iterator.hasNext()){
                Map.Entry<String, Item> item = iterator.next();

                block1.add(
                        "case $L:\n" + "item = new $T(itemView, itemType);\n break;\n",
                        item.getValue().getTypeName(), ClassName.get(item.getValue().getTypeMirror())
                );
            }
            block1.endControlFlow();

            block1.add("if(item != null){\n" +
                    "  if(activity != null){\n" +
                    "    item.setAttachedActivity(activity);\n" +
                    "  } else {\n" +
                    "    item.setAttachedFragment(fragment);\n" +
                    "  }\n" +
                    "}\n" +
                    "return item;\n");

            method1.addCode(block1.build());
            builder.addMethod(method1.build());

            // other getItem method
            MethodSpec.Builder method2 = MethodSpec.methodBuilder(Constants.WATERFALL_ITEMS_METHOD_GET_ITEM)
                    .addModifiers(PUBLIC, STATIC)
                    .addParameter(ClassName.get(Constants.ANDROID_APP_PKG, Constants.ACTIVITY), "activity")
                    .addParameter(TypeName.INT, "itemType")
                    .addParameter(ClassName.get(Constants.ANDROID_VIEW_PKG, Constants.VIEWGROUP), "parent")
                    .returns(baseItem);
            method2.addStatement("return getItem(activity, null, itemType, parent)");
            builder.addMethod(method2.build());

            MethodSpec.Builder method3 = MethodSpec.methodBuilder(Constants.WATERFALL_ITEMS_METHOD_GET_ITEM)
                    .addModifiers(PUBLIC, STATIC)
                    .addParameter(ClassName.get(Constants.ANDROID_SUPPORT_V4_APP_PKG, Constants.FRAGMENT), "fragment")
                    .addParameter(TypeName.INT, "itemType")
                    .addParameter(ClassName.get(Constants.ANDROID_VIEW_PKG, Constants.VIEWGROUP), "parent")
                    .returns(baseItem);
            method3.addStatement("return getItem(null, fragment, itemType, parent)");
            builder.addMethod(method3.build());

            MethodSpec.Builder method4 = MethodSpec.methodBuilder(Constants.WATERFALL_ITEMS_METHOD_GET_ITEM)
                    .addModifiers(PUBLIC, STATIC)
                    .addParameter(TypeName.OBJECT, "context")
                    .addParameter(TypeName.INT, "itemType")
                    .addParameter(ClassName.get(Constants.ANDROID_VIEW_PKG, Constants.VIEWGROUP), "parent")
                    .returns(baseItem);
            method4.addCode("if(context instanceof Activity){\n" +
                    "  return getItem((Activity)context, itemType, parent);\n" +
                    "} else if(context instanceof Fragment){\n" +
                    "  return getItem((Fragment)context, itemType, parent);\n" +
                    "} else {\n" +
                    "  throw new $T(\"Invalid context, must be activity or fragment\");" +
                    "}\n", ClassName.get(InvalidParameterException.class));
            builder.addMethod(method4.build());


            // output WaterfallItem interface java file
            JavaFile.builder(Constants.ANNOTATION_GEN_PKG + "." + module + Constants.ANNOTATION_GEN_WATERFALL,
                    builder.build()).build().writeTo(filer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
