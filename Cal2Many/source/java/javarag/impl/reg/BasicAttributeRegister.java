package javarag.impl.reg;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

import javarag.AttributeEvaluator;
import javarag.AttributeRegister;
import javarag.Bottom;
import javarag.CacheMode;
import javarag.Cached;
import javarag.Circular;
import javarag.Collected;
import javarag.Inherited;
import javarag.Module;
import javarag.NonTerminal;
import javarag.Procedural;
import javarag.Synthesized;
import javarag.TreeTraverser;
import javarag.Types;
import javarag.impl.TreeStructure;

public class BasicAttributeRegister implements AttributeRegister {
	private final EvaluatorBuilder builder;
	private final Set<String> collectionAttributes;
	private final Set<String> attributeNames;

	public BasicAttributeRegister() {
		builder = new EvaluatorBuilder();
		collectionAttributes = new HashSet<>();
		attributeNames = new HashSet<>();
	}

	@Override
	public void register(Class<?>... attributeModules) {
		for (Class<?> module : attributeModules) {
			addDeclarations(module);
		}
		for (Class<?> module : attributeModules) {
			addDefinitions(module);
		}
	}

	private void addDeclarations(Class<?> module) {
		Class<?>[] interfaces = extractModuleImpls(module);
		for (Class<?> iface : interfaces) {
			for (Method decl : iface.getMethods()) {
				String name = decl.getName();
				if (!attributeNames.contains(name)) {
					if (hasAnnotation(decl, Inherited.class)) {
						builder.createInherited(name);
						attributeNames.add(name);
					} else if (hasAnnotation(decl, Synthesized.class)) {
						builder.createSynthesized(name);
						attributeNames.add(name);
					} else if (hasAnnotation(decl, Procedural.class)) {
						builder.createProcedural(name);
						attributeNames.add(name);
					} else if (hasAnnotation(decl, Collected.class)) {
						builder.createCollected(name);
						attributeNames.add(name);
						collectionAttributes.add(name);
					}
					Cached cached = decl.getAnnotation(Cached.class);
					if (cached != null && cached.value() == CacheMode.ALWAYS) {
						builder.setCached(name);
					}
					if (hasAnnotation(decl, Circular.class)) {
						builder.setCircular(name);
					}
					if (hasAnnotation(decl, NonTerminal.class)) {
						builder.setNonTerminal(name);
					}
				}
			}
		}
	}

	private void addDefinitions(Class<?> module) {
		for (Method def : module.getMethods()) {
			Class<?> declaringClass = def.getDeclaringClass();
			if (Module.class != declaringClass && Module.class.isAssignableFrom(declaringClass)) {
				Bottom bottom = def.getAnnotation(Bottom.class);
				if (bottom != null) {
					String name = bottom.value();
					if (name.equals("")) {
						name = def.getName();
					}
					builder.addBottom(name, def);
				} else {
					if (collectionAttributes.contains(def.getName()) && def.getParameterTypes().length == 2) {
						builder.addContribution(def.getName(), def);
					} else {
						builder.addDefinition(def.getName(), def);
					}
				}
			}
		}
	}

	private boolean hasAnnotation(Method decl, Class<? extends Annotation> annotation) {
		return decl.getAnnotation(annotation) != null;
	}

	private Class<?>[] extractModuleImpls(Class<?> klass) {
		Type module = Types.findSuperclass(klass, Module.class);
		Type[] params = Types.getTypeParameters(module);
		return Types.getTypeAsInterfaces(params[0]);
	}

	@Override
	public <T> AttributeEvaluator getEvaluator(T tree, TreeTraverser<T> traverser) {
		return builder.build(new TreeStructure(tree, traverser));
	}

}
