package javarag;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for methods that define inherited attributes.
 * 
 * The first parameter of the method is the node where the attribute is defined
 * and the rest of the parameters are parameters to the attribute. Inherited
 * attributes are available in the descendants of the defining nodes.
 * 
 * @see Synthesized
 * 
 * @author Gustav Cedersjo
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Inherited {

}
