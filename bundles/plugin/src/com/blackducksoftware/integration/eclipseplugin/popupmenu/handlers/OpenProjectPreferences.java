package com.blackducksoftware.integration.eclipseplugin.popupmenu.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

import com.blackducksoftware.integration.eclipseplugin.common.services.DependencyInformationService;
import com.blackducksoftware.integration.eclipseplugin.common.services.ProjectInformationService;
import com.blackducksoftware.integration.eclipseplugin.common.services.WorkspaceInformationService;
import com.blackducksoftware.integration.eclipseplugin.preferences.IndividualProjectPreferences;
import com.blackducksoftware.integration.hub.buildtool.FilePathGavExtractor;

public class OpenProjectPreferences extends AbstractHandler {

    private final char preferencePathSeparatorCharacter = '.';

    @Override
    public Object execute(final ExecutionEvent event) throws ExecutionException {
        final Shell activeShell = HandlerUtil.getActiveShell(event);
        final PreferenceManager mgr = new PreferenceManager(preferencePathSeparatorCharacter);

        final DependencyInformationService depService = new DependencyInformationService();
        final FilePathGavExtractor extractor = new FilePathGavExtractor();
        final ProjectInformationService projService = new ProjectInformationService(depService, extractor);
        final WorkspaceInformationService workspaceService = new WorkspaceInformationService(projService);

        final String projectPrefId = workspaceService.getSelectedProject();
        final IndividualProjectPreferences prefPage = new IndividualProjectPreferences(projectPrefId, projectPrefId);
        final PreferenceNode prefNode = new PreferenceNode(projectPrefId, prefPage);
        mgr.addToRoot(prefNode);
        final PreferenceDialog prefDialog = new PreferenceDialog(activeShell, mgr);
        prefDialog.open();
        return null;
    }

}
