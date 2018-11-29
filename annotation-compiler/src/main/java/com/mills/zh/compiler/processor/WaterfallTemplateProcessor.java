package com.mills.zh.compiler.processor;

import com.google.auto.service.AutoService;
import com.mills.zh.annotation.WaterfallTemplate;
import com.mills.zh.compiler.model.Template;
import com.mills.zh.compiler.utils.Constants;
import com.mills.zh.compiler.utils.Logger;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.lang.instrument.IllegalClassFormatException;
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

import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;

/**
 * Created by zhangmd on 2018/11/20.
 */

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_7)
@SupportedAnnotationTypes({Constants.ANNOTATION_TYPE_WATERFALL_TEMPLATE})
public class WaterfallTemplateProcessor extends AbstractProcessor {
    private static final String TAG = ":WaterfallTemplate";

    private Logger logger;
    private Filer filer;
    private String module;

    private ClassName baseTemplate = ClassName.get(Constants.WATERFALL_COMMON_PKG, Constants.WATERFALL_BASE_TEMPLATE_CLASS);

    private HashMap<String, Template> templates;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);

        logger = new Logger(processingEnvironment.getMessager());
        filer = processingEnvironment.getFiler();
        Map<String, String> options = processingEnv.getOptions();
        if (MapUtils.isNotEmpty(options)) {
            module = options.get("module");
        }
        if (StringUtils.isEmpty(module)) {
            module = "default";
        }

        logger.info(module+TAG, "init");
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        logger.info(module+TAG, "process start");
        if(CollectionUtils.isNotEmpty(set)){
            Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(WaterfallTemplate.class);

            try {
                if(CollectionUtils.isNotEmpty(elements)) {
                    //
                    for(Element element : elements){
                        if(element.getKind() != ElementKind.CLASS){
                            throw new IllegalClassFormatException("WaterfallTemplate: Only class can be annotated width " + Constants.ANNOTATION_TYPE_WATERFALL_TEMPLATE);
                        }
                        if(!element.getModifiers().contains(Modifier.PUBLIC)){
                            throw new IllegalClassFormatException("WaterfallTemplate: the class is not public!");
                        }

                        TypeMirror tm = element.asType();
                        WaterfallTemplate waterfall = element.getAnnotation(WaterfallTemplate.class);
                        logger.info(module+TAG, "Find WaterfallTemplate template annotation:" + tm.toString());

                        String templateName = waterfall.template();
                        if(StringUtils.isEmpty(templateName)){
                            templateName = element.getSimpleName().toString();
                        }

                        if(templates == null){
                            templates = new HashMap<String, Template>();
                        }
                        if(templates.containsKey(templateName)){
                            logger.error(module+TAG, "Find reduplicate template!");
                        } else {
                            Template tmp = new Template();
                            tmp.setTemplate(Constants.WATERFALL_TEMPLATE_PREFIX + templateName.toUpperCase());
                            tmp.setTypeMirror(element.asType());
                            templates.put(templateName, tmp);
                        }
                    }

                    if(templates != null){
                        generateWaterfallTemplateClass();
                    }
                }
            } catch (Exception e) {
                logger.error(module+TAG, e);
            }
            return true;
        }

        return false;
    }

    private void generateWaterfallTemplateClass(){
        try {
            logger.info(module+TAG, "generateWaterfallTemplateClass");
            TypeSpec.Builder builder = TypeSpec.classBuilder(Constants.WATERFALL_TEMPLATE_CLASS)
                    .addModifiers(PUBLIC);

            // init block template types
            Iterator<Map.Entry<String, Template>> iterator = templates.entrySet().iterator();
            while (iterator != null && iterator.hasNext()){

                Map.Entry<String, Template> item = iterator.next();

                FieldSpec field = FieldSpec.builder(String.class, item.getValue().getTemplate())
                        .addModifiers(PUBLIC, STATIC, FINAL)
                        .initializer("$S", item.getKey())
                        .build();

                builder.addField(field);
            }

            // init block template map
            TypeName mapType = ParameterizedTypeName.get(
                    ClassName.get(HashMap.class),
                    ClassName.get(String.class),
                    baseTemplate
                    );
            FieldSpec templateMapField = FieldSpec.builder(mapType, Constants.WATERFALL_TEMPLATES)
                    .addModifiers(PRIVATE, STATIC, FINAL)
                    .initializer("new $T($L)", mapType, templates.size())
                    .build();
            builder.addField(templateMapField);

            iterator = templates.entrySet().iterator();
            CodeBlock.Builder initMapBuilder = CodeBlock.builder();
            while (iterator != null && iterator.hasNext()){
                Map.Entry<String, Template> item = iterator.next();

                initMapBuilder.add(Constants.WATERFALL_TEMPLATES + ".put($S, new $T());\n", item.getKey(), ClassName.get(item.getValue().getTypeMirror()));
            }
            builder.addStaticBlock(initMapBuilder.build());

            // getTemplate method
            MethodSpec.Builder method = MethodSpec.methodBuilder(Constants.WATERFALL_METHOD_GET_TEMPLATE)
                    .addModifiers(PUBLIC, STATIC)
                    .addParameter(String.class, "template")
                    .returns(baseTemplate);
            method.addStatement("return " + Constants.WATERFALL_TEMPLATES + ".get(template)");
            builder.addMethod(method.build());

            // output WaterfallTemplate interface java file
            JavaFile.builder(Constants.WATERFALL_PKG, builder.build()).build().writeTo(filer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
