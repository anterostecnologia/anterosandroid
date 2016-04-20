/*
 * Copyright 2016 Anteros Tecnologia
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.com.anteros.android.core.util;

import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import android.content.Context;
import br.com.anteros.core.scanner.ClassFilter;
import br.com.anteros.core.scanner.ClassPathResourceFilter;
import br.com.anteros.core.scanner.ClassPathScanner;
import dalvik.system.DexFile;

public final class AndroidClassPathScanner extends ClassPathScanner {

	private AndroidClassPathScanner() {
	}

	public static List<URL> scanResources(ClassPathResourceFilter... filters) {
		throw new RuntimeException("Use scanResources with parameter Context.");
	}

	public static List<Class<?>> scanClasses(ClassPathResourceFilter... filters) {
		throw new RuntimeException("Use scanClasses with parameter Context.");
	}

	public static List<Class<?>> scanClasses(Context context, ClassPathResourceFilter... filters) {
		return scanClasses(context.getApplicationInfo().publicSourceDir, filters);
	}

	public static List<Class<?>> scanClasses(String apkName, ClassPathResourceFilter... filters) {
		List<Class<?>> classList = new ArrayList<Class<?>>();

		try {
			DexFile dex = new DexFile(apkName);
			Enumeration<String> entries = dex.entries();

			while (entries.hasMoreElements()) {
				String entry = (String) entries.nextElement();
				try {
					Class<?> clazz = Class.forName(entry);
					if (!clazz.isSynthetic()
							&& Modifier.isPublic(clazz.getModifiers())) {
						clazz.getName();
						clazz.getCanonicalName();
						if (acceptable(clazz, filters)) {
							classList.add(clazz);
						}
					}
				} catch (Exception e) {
				} catch (Error e) {
				}
			}

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		return classList;
	}
	 
	
	public static List<Class<?>> scanClassesImplementsInterface(String apkName, Class<?> interfaceClass) {
		 List<Class<?>> scanClasses = AndroidClassPathScanner.scanClasses(apkName, new ClassFilter().interfaceClass(interfaceClass));
		 return scanClasses;
	}
}
