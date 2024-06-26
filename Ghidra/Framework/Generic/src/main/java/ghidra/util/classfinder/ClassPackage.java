/* ###
 * IP: GHIDRA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ghidra.util.classfinder;

import java.io.File;
import java.io.FileFilter;
import java.util.*;

import ghidra.util.Msg;
import ghidra.util.exception.CancelledException;
import ghidra.util.task.TaskMonitor;

class ClassPackage implements ClassLocation {

	private static final FileFilter CLASS_FILTER =
		pathname -> pathname.getName().endsWith(CLASS_EXT);

	private Set<ClassPackage> children = new HashSet<>();
	private File rootDir;
	private File packageDir;
	private String packageName;
	private Set<ClassFileInfo> classes = new HashSet<>();

	ClassPackage(File rootDir, String packageName, TaskMonitor monitor) throws CancelledException {
		monitor.checkCancelled();
		this.rootDir = rootDir;
		this.packageName = packageName;
		this.packageDir = getPackageDir(rootDir, packageName);
		scanClasses();
		scanSubPackages(monitor);
	}

	private void scanClasses() {

		String path = rootDir.getAbsolutePath();
		Set<String> allClassNames = getAllClassNames();
		for (String className : allClassNames) {
			String epName = ClassSearcher.getExtensionPointSuffix(className);
			if (epName != null) {
				classes.add(new ClassFileInfo(path, className, epName));
			}
		}
	}

	private void scanSubPackages(TaskMonitor monitor) throws CancelledException {

		File[] subdirs = packageDir.listFiles();
		if (subdirs == null) {
			Msg.debug(this, "Directory does not exist: " + packageDir);
			return;
		}

		for (File subdir : subdirs) {
			monitor.checkCancelled();
			if (!subdir.isDirectory()) {
				continue;
			}

			String pkg = subdir.getName();
			if (pkg.contains(".")) {
				// java can't handle dir names with '.'-- it conflicts with the package structure
				continue;
			}

			if (packageName.length() > 0) {
				pkg = packageName + "." + pkg;
			}

			monitor.setMessage("Scanning package: " + pkg);
			children.add(new ClassPackage(rootDir, pkg, monitor));
		}
	}

	private File getPackageDir(File lRootDir, String lPackageName) {
		return new File(lRootDir, lPackageName.replace('.', File.separatorChar));
	}

	@Override
	public void getClasses(List<ClassFileInfo> list, TaskMonitor monitor)
			throws CancelledException {

		list.addAll(classes);

		Iterator<ClassPackage> it = children.iterator();
		while (it.hasNext()) {
			monitor.checkCancelled();
			ClassPackage subPkg = it.next();
			subPkg.getClasses(list, monitor);
		}
	}

	private Set<String> getAllClassNames() {

		File[] files = packageDir.listFiles(CLASS_FILTER);
		if (files == null) {
			return Collections.emptySet();
		}

		Set<String> results = new HashSet<>(files.length);
		for (File file : files) {
			String name = file.getName();
			name = name.substring(0, name.length() - 6);
			if (packageName.length() > 0) {
				name = packageName + "." + name;
			}
			results.add(name);
		}
		return results;
	}

	@Override
	public String toString() {
		return packageDir.toString();
	}
}
