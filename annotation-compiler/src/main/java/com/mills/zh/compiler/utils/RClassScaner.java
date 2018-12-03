package com.mills.zh.compiler.utils;

import com.mills.zh.annotation.waterfall.WaterfallItem;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.sun.source.tree.ClassTree;
import com.sun.source.util.Trees;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeScanner;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/**
 * Created by zhangmd on 2018/11/27.
 */

public class RClassScaner {

    private static final List<String> SUPPORTED_TYPES = Arrays.asList(
            "layout"
    );

    private final Map<QualifiedId, Id> symbols = new LinkedHashMap<>();

    private Trees trees;
    private Elements elementUtils;
    private Types typeUtils;

    public RClassScaner(Trees trees,  Elements elementUtils, Types typeUtils){
        this.trees = trees;
        this.elementUtils = elementUtils;
        this.typeUtils = typeUtils;
    }

    public void scan(RoundEnvironment env){
        if (trees == null) return;

        RClassScanner scanner = new RClassScanner();

        for (Class<? extends Annotation> annotation : getSupportedAnnotations()) {
            // 遍历注解标注的类element
            for (Element element : env.getElementsAnnotatedWith(annotation)) {
                // 获取注解类的tree：@WaterfallItem(type = "still",layout = R2.layout.waterfall_item_media_h_layout)
                JCTree tree = (JCTree) trees.getTree(element, getMirror(element, annotation));
                if (tree != null) { // tree can be null if the references are compiled types and not source
                    // 获取注解标注的class的包名
                    String respectivePackageName =
                            elementUtils.getPackageOf(element).getQualifiedName().toString();
                    scanner.setCurrentPackageName(respectivePackageName);
                    tree.accept(scanner);
                }
            }
        }

        for (Map.Entry<String, Set<String>> packageNameToRClassSet : scanner.getRClasses().entrySet()) {
            // 注解类标注的class类包名
            String respectivePackageName = packageNameToRClassSet.getKey();
            // 遍历该包名对应的R2类名
            for (String rClass : packageNameToRClassSet.getValue()) {
                Element element;
                try {
                    element = elementUtils.getTypeElement(rClass);
                } catch (MirroredTypeException mte) {
                    element = typeUtils.asElement(mte.getTypeMirror());
                }
                // 获取R2类的tree
                JCTree tree = (JCTree) trees.getTree(element);
                if (tree != null) { // tree can be null if the references are compiled types and not source
                    IdScanner idScanner = new IdScanner(symbols, elementUtils.getPackageOf(element)
                            .getQualifiedName().toString(), respectivePackageName);
                    tree.accept(idScanner);
                } else {
                    parseCompiledR(respectivePackageName, (TypeElement) element);
                }
            }
        }
    }

    private void parseCompiledR(String respectivePackageName, TypeElement rClass) {
        for (Element element : rClass.getEnclosedElements()) {
            String innerClassName = element.getSimpleName().toString();
            if (SUPPORTED_TYPES.contains(innerClassName)) {
                for (Element enclosedElement : element.getEnclosedElements()) {
                    if (enclosedElement instanceof VariableElement) {
                        VariableElement variableElement = (VariableElement) enclosedElement;
                        Object value = variableElement.getConstantValue();

                        if (value instanceof Integer) {
                            int id = (Integer) value;
                            ClassName rClassName =
                                    ClassName.get(elementUtils.getPackageOf(variableElement).toString(), "R",
                                            innerClassName);
                            String resourceName = variableElement.getSimpleName().toString();
                            QualifiedId qualifiedId = new QualifiedId(respectivePackageName, id);
                            symbols.put(qualifiedId, new Id(id, rClassName, resourceName));
                        }
                    }
                }
            }
        }
    }


    private static Set<Class<? extends Annotation>> getSupportedAnnotations() {
        Set<Class<? extends Annotation>> annotations = new LinkedHashSet<>();
        annotations.add(WaterfallItem.class);
        return annotations;
    }

    private static AnnotationMirror getMirror(Element element,
                                              Class<? extends Annotation> annotation) {
        for (AnnotationMirror annotationMirror : element.getAnnotationMirrors()) {
            if (annotationMirror.getAnnotationType().toString().equals(annotation.getCanonicalName())) {
                return annotationMirror;
            }
        }
        return null;
    }

    public Id getId(QualifiedId qualifiedId) {
        if (symbols.get(qualifiedId) == null) {
            symbols.put(qualifiedId, new Id(qualifiedId.id));
        }
        return symbols.get(qualifiedId);
    }

    public QualifiedId elementToQualifiedId(Element element, int id) {
        return new QualifiedId(elementUtils.getPackageOf(element).getQualifiedName().toString(), id);
    }

    private static class RClassScanner extends TreeScanner {
        // Maps the currently evaulated rPackageName to R Classes
        private final Map<String, Set<String>> rClasses = new LinkedHashMap<>();
        private String currentPackageName;

        @Override public void visitSelect(JCTree.JCFieldAccess jcFieldAccess) {
            // jcFieldAccess：R2.layout.waterfall_item_media_h_layout    ???为何只visit该类型
            // symbol：waterfall_item_media_h_layout
            Symbol symbol = jcFieldAccess.sym;
            // 判断symbol是否有所属的类，以及所属类是否有所属类...
            if (symbol != null
                    // layout子类
                    && symbol.getEnclosingElement() != null
                    // R2类
                    && symbol.getEnclosingElement().getEnclosingElement() != null
                    // R2类
                    && symbol.getEnclosingElement().getEnclosingElement().enclClass() != null) {
                Set<String> rClassSet = rClasses.get(currentPackageName);
                if (rClassSet == null) {
                    rClassSet = new HashSet<>();
                    rClasses.put(currentPackageName, rClassSet);
                }
                // [关键]获取R2类的全名称
                rClassSet.add(symbol.getEnclosingElement().getEnclosingElement().enclClass().className());
            }
        }

        Map<String, Set<String>> getRClasses() {
            return rClasses;
        }

        void setCurrentPackageName(String respectivePackageName) {
            this.currentPackageName = respectivePackageName;
        }
    }

    private static class IdScanner extends TreeScanner {
        private final Map<QualifiedId, Id> ids;
        private final String rPackageName;          // R2类包名
        private final String respectivePackageName; // 注解标注的class包名

        IdScanner(Map<QualifiedId, Id> ids, String rPackageName, String respectivePackageName) {
            this.ids = ids;
            this.rPackageName = rPackageName;
            this.respectivePackageName = respectivePackageName;
        }

        @Override public void visitClassDef(JCTree.JCClassDecl jcClassDecl) {
            // 遍历R2类中的class对象
            for (JCTree tree : jcClassDecl.defs) {
                if (tree instanceof ClassTree) {
                    ClassTree classTree = (ClassTree) tree;
                    String className = classTree.getSimpleName().toString();
                    // 找到关心的资源class，比如layout
                    if (SUPPORTED_TYPES.contains(className)) {
                        // 获取R类中具体子类的全名称：xxx.xxx.R.layout
                        ClassName rClassName = ClassName.get(rPackageName, "R", className);
                        VarScanner scanner = new VarScanner(ids, rClassName, respectivePackageName);
                        ((JCTree) classTree).accept(scanner);
                    }
                }
            }
        }
    }

    private static class VarScanner extends TreeScanner {
        private final Map<QualifiedId, Id> ids;
        private final ClassName className;
        private final String respectivePackageName;

        private VarScanner(Map<QualifiedId, Id> ids, ClassName className,
                           String respectivePackageName) {
            this.ids = ids;
            this.className = className;
            this.respectivePackageName = respectivePackageName;
        }

        @Override public void visitVarDef(JCTree.JCVariableDecl jcVariableDecl) {
            // 遍历layout类的成员变量
            if ("int".equals(jcVariableDecl.getType().toString())) {
                // 取得R2类中layout类中的资源id、资源名
                int id = Integer.valueOf(jcVariableDecl.getInitializer().toString());
                String resourceName = jcVariableDecl.getName().toString();
                QualifiedId qualifiedId = new QualifiedId(respectivePackageName, id);
                ids.put(qualifiedId, new Id(id, className, resourceName));
            }
        }
    }

    public static final class Id {
        private static final ClassName ANDROID_R = ClassName.get("android", "R");

        final int value;
        final CodeBlock code;
        final boolean qualifed;

        Id(int value) {
            this.value = value;
            this.code = CodeBlock.of("$L", value);
            this.qualifed = false;
        }

        /**
         *
         * @param value             R2中的资源id值
         * @param className         如xxx.xxx.R.layout类名
         * @param resourceName      R2中的资源名称
         */
        Id(int value, ClassName className, String resourceName) {
            this.value = value;
            this.code = className.topLevelClassName().equals(ANDROID_R)
                    ? CodeBlock.of("$L.$N", className, resourceName)
                    : CodeBlock.of("$T.$N", className, resourceName);
            this.qualifed = true;
        }

        public int getValue() {
            return value;
        }

        public CodeBlock getCode() {
            return code;
        }

        @Override public boolean equals(Object o) {
            return o instanceof Id && value == ((Id) o).value;
        }

        @Override public int hashCode() {
            return value;
        }

        @Override public String toString() {
            throw new UnsupportedOperationException("Please use value or code explicitly");
        }
    }

    public static class QualifiedId {
        final String packageName;
        final int id;

        QualifiedId(String packageName, int id) {
            this.packageName = packageName;
            this.id = id;
        }

        @Override public String toString() {
            return "QualifiedId{packageName='" + packageName + "', id=" + id + '}';
        }

        @Override public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof QualifiedId)) return false;
            QualifiedId other = (QualifiedId) o;
            return id == other.id
                    && packageName.equals(other.packageName);
        }

        @Override public int hashCode() {
            int result = packageName.hashCode();
            result = 31 * result + id;
            return result;
        }
    }
}
