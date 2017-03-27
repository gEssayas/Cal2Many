package javarag;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for methods that define synthesized attributes.
 * 
 * The first parameter of the method is the node where the attribute is defined
 * and the rest of the parameters are parameters to the attribute. Synthesized
 * attributes are available only in the nodes where they are defined.
 * 
 * @see Inherited
 * 
 * @author Gustav Cedersjo
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Synthesized {

}
