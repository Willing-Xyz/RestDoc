package cn.willingxyz.restdoc.beanvalidation;

import cn.willingxyz.restdoc.core.models.PropertyItem;
import cn.willingxyz.restdoc.core.models.PropertyModel;
import cn.willingxyz.restdoc.core.models.TypeContext;
import cn.willingxyz.restdoc.core.parse.IPropertyPostProcessor;

import javax.validation.Valid;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedParameterizedType;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.ParameterizedType;
import java.util.List;

/**
 * 基于注解的属性
 */
public abstract class AbstractPropertyPostProcessor<T extends Annotation> implements IPropertyPostProcessor {
    @Override
    public void postProcess(PropertyModel propertyModel, TypeContext typeContext) {
        if (!cascadeValid(propertyModel, typeContext)) return;
        postProcessInternal(propertyModel);
    }

    private boolean cascadeValid(PropertyModel propertyModel, TypeContext typeContext) {
        PropertyItem parent = propertyModel.getParentPropertyItem();
        if (parent == null) return true;

        Valid validAnno = parent.getAnnotation(Valid.class);
        if (validAnno != null)
            return true;

        AnnotatedType annotatedType = null;
        if (parent.getField() != null) {
            annotatedType = parent.getField().getAnnotatedType();
        } else if (parent.getGetMethod() != null) {
            annotatedType = parent.getGetMethod().getAnnotatedParameterTypes()[0];
        } else if (parent.getSetMethod() != null) {
            annotatedType = parent.getGetMethod().getAnnotatedReturnType();
        }
        if (annotatedType != null) {
            if (annotatedType instanceof AnnotatedParameterizedType) {
                AnnotatedParameterizedType annotatedParameterizedType = (AnnotatedParameterizedType) annotatedType;
                if (annotatedParameterizedType.getType() instanceof ParameterizedType) {
                    Class clazz = (Class) ((ParameterizedType) annotatedParameterizedType.getType()).getRawType();
                    if (List.class.isAssignableFrom(clazz)) {
                        if (annotatedParameterizedType.getAnnotatedActualTypeArguments()[0].getAnnotation(Valid.class) != null)
                            return true;
                    }
                }
            }
        }
        return false;
    }

    protected abstract void postProcessInternal(PropertyModel propertyModel);
}
