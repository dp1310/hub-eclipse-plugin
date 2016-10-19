package com.blackducksoftware.integration.eclipseplugin.preferences;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.blackducksoftware.integration.eclipseplugin.common.constants.ProjectSpecificPreferenceNames;
import com.blackducksoftware.integration.eclipseplugin.popupmenu.Activator;

/*
 * Class that implements the individual project preferences page
 */
public class IndividualProjectPreferences extends PreferencePage implements IWorkbenchPreferencePage {

	private final String projectId;
	private BooleanFieldEditor displayWarnings;

	public IndividualProjectPreferences(final String id) {
		super();
		projectId = id;
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
	}

	public IndividualProjectPreferences(final String id, final String title) {
		super(title);
		projectId = id;
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
	}

	public IndividualProjectPreferences(final String id, final String title, final ImageDescriptor image) {
		super(title, image);
		projectId = id;
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
	}

	@Override
	public void init(final IWorkbench workbench) {
		// TODO Auto-generated method stub
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
	}

	@Override
	protected Control createContents(final Composite parent) {
		final Composite displayWarningsComposite = new Composite(parent, SWT.LEFT);
		displayWarningsComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		displayWarningsComposite.setLayout(new GridLayout());

		final String displayWarningsPropertyId = StringUtils
				.join(new String[] { projectId, ProjectSpecificPreferenceNames.ACTIVATE_PROJECT }, ':');
		displayWarnings = new BooleanFieldEditor(displayWarningsPropertyId, "Activate Black Duck scan",
				displayWarningsComposite);
		displayWarnings.setPage(this);
		displayWarnings.setPreferenceStore(getPreferenceStore());
		displayWarnings.load();
		return displayWarningsComposite;
	}

	@Override
	public void performApply() {
		storeValues();
	}

	@Override
	public boolean performOk() {
		if (super.performOk()) {
			storeValues();
			return true;
		} else {
			return false;
		}
	}

	private void storeValues() {
		final IPreferenceStore prefStore = getPreferenceStore();
		prefStore.setValue(displayWarnings.getPreferenceName(), displayWarnings.getBooleanValue());
	}

	@Override
	protected void performDefaults() {

		displayWarnings.loadDefault();
		super.performDefaults();
	}

}
