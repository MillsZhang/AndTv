package com.mills.zh.plugin;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.type.Type;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import javax.lang.model.element.Modifier;

/**
 * Created by zhangmd on 2018/11/27.
 */

public class FinalRClassBuilder {

    private static final String SUPPORT_ANNOTATION_PACKAGE = "android.support.annotation";
    private static final String[] SUPPORTED_TYPES = {
            "layout"
    };

    private FinalRClassBuilder() {}

    public static void brewJava(File rFile, File outputDir, String packageName, String className)
            throws Exception {
        CompilationUnit compilationUnit = JavaParser.parse(rFile);
        TypeDeclaration resourceClass = compilationUnit.getTypes().get(0);

        TypeSpec.Builder result =
                TypeSpec.classBuilder(className).addModifiers(Modifier.PUBLIC).addModifiers(Modifier.FINAL);

        for (Node node : resourceClass.getChildNodes()) {
            if (node instanceof ClassOrInterfaceDeclaration) {
                addResourceType(Arrays.asList(SUPPORTED_TYPES), result, (ClassOrInterfaceDeclaration) node);
            }
        }

        JavaFile finalR = JavaFile.builder(packageName, result.build())
                .addFileComment("Generated code from AndTv gradle plugin. Do not modify!")
                .build();

        finalR.writeTo(outputDir);
    }

    private static void addResourceType(List<String> supportedTypes, TypeSpec.Builder result,
                                        ClassOrInterfaceDeclaration node) {
        if (!supportedTypes.contains(node.getNameAsString())) {
            return;
        }

        String type = node.getNameAsString();
        TypeSpec.Builder resourceType = TypeSpec.classBuilder(type).addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL);

        for (BodyDeclaration field : node.getMembers()) {
            if (field instanceof FieldDeclaration) {
                FieldDeclaration declaration = (FieldDeclaration) field;
                // Check that the field is an Int because styleable also contains Int arrays which can't be
                // used in annotations.
                if (isInt(declaration)) {
                    addResourceField(resourceType, declaration.getVariables().get(0),
                            getSupportAnnotationClass(type));
                }
            }
        }

        result.addType(resourceType.build());
    }

    private static boolean isInt(FieldDeclaration field) {
        Type type = field.getCommonType();
        return type instanceof PrimitiveType
                && ((PrimitiveType) type).getType() == PrimitiveType.Primitive.INT;
    }

    private static void addResourceField(TypeSpec.Builder resourceType, VariableDeclarator variable,
                                         ClassName annotation) {
        String fieldName = variable.getNameAsString();
        String fieldValue = variable.getInitializer().map(Node::toString).orElse(null);
        FieldSpec.Builder fieldSpecBuilder = FieldSpec.builder(int.class, fieldName)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .initializer(fieldValue);

//        if (annotation != null) {
//            fieldSpecBuilder.addAnnotation(annotation);
//        }

        resourceType.addField(fieldSpecBuilder.build());
    }

    private static ClassName getSupportAnnotationClass(String type) {
        return ClassName.get(SUPPORT_ANNOTATION_PACKAGE, capitalize(type) + "Res");
    }

    private static String capitalize(String word) {
        return Character.toUpperCase(word.charAt(0)) + word.substring(1);
    }
}
