package com.blackducksoftware.integration.eclipseplugin.internal.listeners;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
// import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.ElementChangedEvent;
import org.eclipse.jdt.core.IElementChangedListener;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaElementDelta;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

import com.blackducksoftware.integration.build.Gav;
import com.blackducksoftware.integration.build.GavTypeEnum;
import com.blackducksoftware.integration.build.GavWithType;
import com.blackducksoftware.integration.build.utils.FilePathGavExtractor;
import com.blackducksoftware.integration.eclipseplugin.common.constants.ClasspathVariables;
import com.blackducksoftware.integration.eclipseplugin.common.services.DependencyInformationService;
import com.blackducksoftware.integration.eclipseplugin.internal.ProjectDependencyInformation;

public class ProjectDependenciesChangedListener implements IElementChangedListener {
    private final ProjectDependencyInformation information;

    private final FilePathGavExtractor extractor;

    private final DependencyInformationService depService;

    public ProjectDependenciesChangedListener(final ProjectDependencyInformation information,
            final FilePathGavExtractor extractor, final DependencyInformationService depService) {
        this.information = information;
        this.extractor = extractor;
        this.depService = depService;
    }

    @Override
    public void elementChanged(final ElementChangedEvent event) {
        visit(event.getDelta());
    }

    public String getProjectNameFromElement(final IJavaElement el) throws CoreException {
        final IJavaProject javaProj = el.getJavaProject();
        if (javaProj != null) {
            final IProject proj = javaProj.getProject();
            if (proj != null) {
                final IProjectDescription description = proj.getDescription();
                if (description != null) {
                    return description.getName();
                }
            }
        }
        return null;
    }

    public void removeDependency(final IJavaElement el) throws CoreException {
        final String projName = getProjectNameFromElement(el);
        if (projName != null) {
            final String OSSpecificFilepath = el.getPath().toOSString();
            if (depService.isGradleDependency(OSSpecificFilepath)) {
                final Gav gav = extractor.getGradlePathGav(OSSpecificFilepath);
                information.removeWarningFromProject(projName, gav);
            } else if (depService.isMavenDependency(OSSpecificFilepath)) {
                final String mavenPath = JavaCore.getClasspathVariable(ClasspathVariables.MAVEN).toOSString();
                final Gav gav = extractor.getMavenPathGav(OSSpecificFilepath, mavenPath);
                information.removeWarningFromProject(projName, gav);
            }
        }

    }

    public void addDependency(final IJavaElement el) throws CoreException {
        final String projName = getProjectNameFromElement(el);
        if (projName != null) {
            final String OSSpecificFilepath = el.getPath().toOSString();
            if (depService.isGradleDependency(OSSpecificFilepath)) {
                final Gav gav = extractor.getGradlePathGav(OSSpecificFilepath);
                information.addWarningToProject(projName, new GavWithType(gav, GavTypeEnum.MAVEN));
            } else if (depService.isMavenDependency(OSSpecificFilepath)) {
                final String mavenPath = JavaCore.getClasspathVariable(ClasspathVariables.MAVEN).toOSString();
                final Gav gav = extractor.getMavenPathGav(OSSpecificFilepath, mavenPath);
                information.addWarningToProject(projName, new GavWithType(gav, GavTypeEnum.MAVEN));
            }
        }
    }

    private void visit(final IJavaElementDelta delta) {
        final IJavaElement el = delta.getElement();
        switch (el.getElementType()) {
        case IJavaElement.JAVA_MODEL: {
            visitChildren(delta);
            break;
        }
        case IJavaElement.JAVA_PROJECT: {
            if (isClasspathChanged(delta.getFlags())) {
                visitChildren(delta);
            }
            break;
        }
        case IJavaElement.PACKAGE_FRAGMENT_ROOT: {
            if ((delta.getFlags() & IJavaElementDelta.F_REMOVED_FROM_CLASSPATH) != 0) {
                try {
                    removeDependency(el);
                } catch (final CoreException e) {
                }
            } else if ((delta.getKind() & (IJavaElementDelta.ADDED | IJavaElementDelta.CHANGED)) != 0) {
                try {
                    addDependency(el);
                } catch (final CoreException e) {
                }
            }
            break;
        }
        default: {
            break;
        }
        }

    }

    private boolean isClasspathChanged(final int flags) {
        return 0 != (flags & (IJavaElementDelta.F_CLASSPATH_CHANGED | IJavaElementDelta.F_RESOLVED_CLASSPATH_CHANGED));
    }

    private void visitChildren(final IJavaElementDelta delta) {
        for (final IJavaElementDelta c : delta.getAffectedChildren()) {
            visit(c);
            //TODO remove
            System.out.println("child affected");
        }
    }

}
