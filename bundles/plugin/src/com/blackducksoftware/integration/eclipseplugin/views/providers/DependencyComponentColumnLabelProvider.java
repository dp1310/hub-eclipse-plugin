package com.blackducksoftware.integration.eclipseplugin.views.providers;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;

import com.blackducksoftware.integration.eclipseplugin.common.constants.PathsToIconFiles;
import com.blackducksoftware.integration.eclipseplugin.startup.Activator;
import com.blackducksoftware.integration.eclipseplugin.views.providers.utils.GavWithParentProject;

public class DependencyComponentColumnLabelProvider extends DependencyTreeViewLabelProvider implements IStyledLabelProvider {

	@Override
	public String getText(Object input) {
        if (input instanceof GavWithParentProject) {
            String text = "" + ((GavWithParentProject) input).getGav().toString();
            return text;
        }
        if (input instanceof String) {
            return (String) input;
        }
        return "";
	}

	@Override
	public String getTitle() {
		return "Component";
	}
	
    @Override
    public Image getImage(Object input) {
        if (input instanceof GavWithParentProject) {
            ImageDescriptor descriptor;
            if (!((GavWithParentProject) input).hasVulns()) {
                descriptor = Activator.getImageDescriptor(PathsToIconFiles.GREEN_CHECK);
            } else {
                descriptor = Activator.getImageDescriptor(PathsToIconFiles.RED_X);
            }
            return descriptor == null ? null : descriptor.createImage();
        }
        return null;
    }

    @Override
    public StyledString getStyledText(Object element) {
        String text = getText(element);
        return new StyledString(text);
    }

}
