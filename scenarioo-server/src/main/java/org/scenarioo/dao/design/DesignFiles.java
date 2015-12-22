/* scenarioo-server
 * Copyright (C) 2014, scenarioo.org Development Team
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.scenarioo.dao.design;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.apache.batik.ext.awt.image.codec.png.PNGRegistryEntry;
import org.apache.batik.ext.awt.image.spi.ImageTagRegistry;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.scenarioo.api.util.files.FilesUtil;
import org.scenarioo.api.util.xml.ScenarioDocuXMLFileUtil;
import org.scenarioo.model.design.entities.Issue;
import org.scenarioo.model.design.entities.ScenarioSketch;
import org.scenarioo.model.design.entities.StepSketch;

/**
 * Represents the file structure of the design domain.
 */
public class DesignFiles {

	private static final Logger LOGGER = Logger.getLogger(DesignFiles.class);

	private static final String DIRECTORY_NAME_SCENARIOSKETCH_STEPSKETCHES = "stepSketches";

	private static final String DIRECTORY_NAME_STEPSKETCH_SVG = "svg";

	private static final String FILE_NAME_SCENARIOSKETCH = "scenarioSketch.xml";

	private static final String FILE_NAME_ISSUE = "issue.xml";

	private static final String FILE_NAME_BRANCH = "branch.xml";

	private final File rootDirectory;

	private final PNGTranscoder transcoder = new PNGTranscoder();

	public DesignFiles(final File rootDirectory) {
		this.rootDirectory = rootDirectory;
	}

	public void createRootIfNecessary() {
		if (this.rootDirectory.exists() == false) {
			this.rootDirectory.mkdirs();
		}
	}

	public void assertRootDirectoryExists() {
		if (!rootDirectory.exists()) {
			throw new IllegalArgumentException("Directory for design storage does not exist: "
					+ rootDirectory.getAbsolutePath());
		}
	}

	public File getRootDirectory() {
		assertRootDirectoryExists();
		return rootDirectory;
	}

	public File getBranchDirectory(final String branchName) {
		return new File(rootDirectory, FilesUtil.encodeName(branchName));
	}

	public File getBranchFile(final String branchName) {
		return new File(getBranchDirectory(branchName), FILE_NAME_BRANCH);
	}

	public List<File> getBranchFiles() {
		return FilesUtil.getListOfFilesFromSubdirs(rootDirectory, FILE_NAME_BRANCH);
	}

	public File getIssueDirectory(final String branchName, final String issueName) {
		final File issueDirectory = new File(getBranchDirectory(branchName), FilesUtil.encodeName(issueName));
		return issueDirectory;
	}

	public File getIssueFile(final String branchName, final String issueName) {
		return new File(getIssueDirectory(branchName, issueName), FILE_NAME_ISSUE);
	}

	public List<File> getIssueFiles(final String branchName) {
		return FilesUtil.getListOfFilesFromSubdirs(getBranchDirectory(branchName), FILE_NAME_ISSUE);
	}

	public Boolean deleteIssue(final String branchName, final String issueId) {
		try {
			FileUtils.forceDelete(getIssueDirectory(branchName, issueId));
		} catch (final IOException e) {
			LOGGER.error(e.getMessage());
			return false;
		}
		return true;
	}

	public File getScenarioSketchDirectory(final String branchName, final String issueName,
			final String scenarioSketchName) {
		final File scenarioSketchDirectory = new File(getIssueDirectory(branchName, issueName),
				FilesUtil.encodeName(scenarioSketchName));
		return scenarioSketchDirectory;
	}

	public File getScenarioSketchFile(final String branchName, final String issueName, final String scenarioSketchName) {
		return new File(getScenarioSketchDirectory(branchName, issueName, scenarioSketchName), FILE_NAME_SCENARIOSKETCH);
	}

	public List<File> getScenarioSketchFiles(final String branchName, final String issueName) {
		return FilesUtil.getListOfFilesFromSubdirs(getIssueDirectory(branchName, issueName), FILE_NAME_SCENARIOSKETCH);
	}

	public File getStepSketchesDirectory(final String branchName, final String issueName, final String scenarioSketchName) {
		final File stepSketchesDirectory = new File(
				getScenarioSketchDirectory(branchName, issueName, scenarioSketchName),
				DIRECTORY_NAME_SCENARIOSKETCH_STEPSKETCHES);
		return stepSketchesDirectory;
	}

	public File getStepSketchFile(final String branchName, final String issueName, final String scenarioSketchId,
			final String stepSketchId) {
		return new File(getStepSketchesDirectory(branchName, issueName, scenarioSketchId),
				stepSketchId + ".xml");
	}

	public File getSVGDirectory(final String branchName, final String issueName, final String scenarioSketchId) {
		final File svgDirectory = new File(getStepSketchesDirectory(branchName, issueName, scenarioSketchId),
				DIRECTORY_NAME_STEPSKETCH_SVG);
		return svgDirectory;
	}

	public File getSVGFile(final String branchName, final String issueId, final String scenarioSketchId,
			final String svgFilename) {
		return new File(getSVGDirectory(branchName, issueId, scenarioSketchId), svgFilename);
	}

	public File getPNGFile(final String branchName, final String issueId, final String scenarioSketchId,
			final String pngFilename) {
		return new File(getSVGDirectory(branchName, issueId, scenarioSketchId), pngFilename);
	}

	public List<File> getStepSketchFiles(final String branchName, final String issueName, final String scenarioSketchId) {
		return FilesUtil.getListOfFiles(getStepSketchesDirectory(branchName, issueName, scenarioSketchId));
	}

	/**
	 * @return A {@link File} object pointing to the PNG file of the step screenshot. The method does not care whether
	 *         the file actually exists.
	 */
	public File getOriginalScreenshotFile(final String branchName, final String issueId,
			final String scenarioSketchId, final int stepSketchId) {
		return new File(getSVGDirectory(branchName, issueId, scenarioSketchId),
				stepSketchId + ".png");
	}

	public boolean createIssueDirectory(final String branchName, final String issueName) {
		final File issueDirectory = new File(getBranchDirectory(branchName), FilesUtil.encodeName(issueName));
		final boolean isCreated = issueDirectory.mkdir();
		if (!isCreated) {
			LOGGER.error("Issue directory not created.");
		}
		return isCreated;
	}

	public File createIssueFile(final String branchName, final String issueName) {
		final File issueFile = new File(getIssueDirectory(branchName, issueName), FILE_NAME_ISSUE);
		try {
			issueFile.createNewFile();
			return issueFile;
		} catch (final IOException e) {
			LOGGER.error("Issue file not created.");
		}
		return issueFile;
	}

	public void writeIssueToFile(final String branchName, final Issue issue) {
		createBranchDirectoryIfNecessary(branchName);
		createIssueDirectory(branchName, issue.getIssueId());
		final File destinationFile = createIssueFile(branchName, issue.getIssueId());
		ScenarioDocuXMLFileUtil.marshal(issue, destinationFile);
	}

	private void createBranchDirectoryIfNecessary(final String branchName) {
		final File branchFolder = new File(rootDirectory, FilesUtil.encodeName(branchName));
		// Make sure design root folder exists and change to mkdir() here.
		branchFolder.mkdirs();
	}

	public void updateIssue(final String branchName, final Issue issue) {
		final File destinationFile = getIssueFile(branchName, issue.getIssueId());
		ScenarioDocuXMLFileUtil.marshal(issue, destinationFile);
	}

	public File createScenarioSketchFile(final String branchName, final String issueId, final String scenarioSketchId) {
		final File scenarioSketchFile = new File(getScenarioSketchDirectory(branchName, issueId, scenarioSketchId),
				FILE_NAME_SCENARIOSKETCH);
		try {
			scenarioSketchFile.createNewFile();
			return scenarioSketchFile;
		} catch (final IOException e) {
			LOGGER.error("ScenarioSketch file not created.");
		}
		return scenarioSketchFile;
	}

	public boolean createScenarioSketchDirectory(final String branchName, final String issueId,
			final String scenarioSketchId) {
		final File scenarioSketchDir = getScenarioSketchDirectory(branchName, issueId, scenarioSketchId);
		final boolean isCreated = scenarioSketchDir.mkdirs();
		if (!isCreated) {
			LOGGER.error("ScenarioSketch directory not created.");
		}
		return isCreated;
	}

	public void writeScenarioSketchToFile(final String branchName, final String issueId,
			final ScenarioSketch scenarioSketch) {
		createScenarioSketchDirectory(branchName, issueId, scenarioSketch.getScenarioSketchId());
		final File destinationFile = createScenarioSketchFile(branchName, issueId, scenarioSketch.getScenarioSketchId());
		ScenarioDocuXMLFileUtil.marshal(scenarioSketch, destinationFile);
	}

	public void updateScenarioSketch(final String branchName, final ScenarioSketch scenarioSketch) {
		final File destinationFile = getScenarioSketchFile(branchName, scenarioSketch.getIssueId(),
				scenarioSketch.getScenarioSketchId());
		ScenarioDocuXMLFileUtil.marshal(scenarioSketch, destinationFile);
	}

	public boolean createStepSketchDirectory(final String branchName, final String issueName,
			final String scenarioSketchId) {
		final File stepSketchDir = getStepSketchesDirectory(branchName, issueName, scenarioSketchId);
		final boolean isCreated = stepSketchDir.mkdirs();
		if (!isCreated) {
			LOGGER.error("StepSketch directory not created.");
			LOGGER.error(stepSketchDir.getAbsolutePath());
		}
		return isCreated;
	}

	public File createStepSketchFile(final String branchName, final String issueName,
			final String scenarioSketchId, final String stepSketchId, final StepSketch stepSketch) {
		final File stepSketchFile = new File(getStepSketchesDirectory(branchName, issueName, scenarioSketchId),
				stepSketchId + ".xml");
		try {
			stepSketchFile.createNewFile();
			return stepSketchFile;
		} catch (final IOException e) {
			LOGGER.error("StepSketch file not created.");
		}
		return stepSketchFile;
	}

	public void writeStepSketchToFile(final String branchName, final String issueId,
			final String scenarioSketchId, final StepSketch stepSketch) {
		createStepSketchDirectory(branchName, issueId, scenarioSketchId);
		final File destinationFile = createStepSketchFile(branchName, issueId, scenarioSketchId,
				stepSketch.getStepSketchId(), stepSketch);
		ScenarioDocuXMLFileUtil.marshal(stepSketch, destinationFile);
	}

	public void updateStepSketch(final String branchName, final StepSketch stepSketch) {
		final File destinationFile = getStepSketchFile(branchName, stepSketch.getIssueId(),
				stepSketch.getScenarioSketchId(), stepSketch.getStepSketchId());
		ScenarioDocuXMLFileUtil.marshal(stepSketch, destinationFile);
	}

	public boolean createStepSketchSVGDirectory(final String branchName, final String issueID,
			final String scenarioSketchId) {
		final File stepSketchSVGDir = new File(getStepSketchesDirectory(branchName, issueID, scenarioSketchId),
				DIRECTORY_NAME_STEPSKETCH_SVG);
		final boolean isCreated = stepSketchSVGDir.mkdirs();
		if (!isCreated && !stepSketchSVGDir.exists()) {
			LOGGER.error("StepSketch SVG directory not created: " + stepSketchSVGDir);
		}
		return isCreated;
	}

	public File getStepSketchSVGDirectory(final String branchName, final String issueId,
			final String scenarioSketchId) {
		return new File(getStepSketchesDirectory(branchName, issueId, scenarioSketchId), DIRECTORY_NAME_STEPSKETCH_SVG);
	}

	public void writeSVGToFile(final String branchName, final String issueId,
			final String scenarioSketchId, final StepSketch stepSketch) {
		createStepSketchSVGDirectory(branchName, issueId, scenarioSketchId);
		final String svgFilename = "sketch.svg";
		final File stepSketchSVGFile = new File(getStepSketchSVGDirectory(branchName, issueId, scenarioSketchId),
				svgFilename);
		storeSvgFile(branchName, issueId, scenarioSketchId, stepSketch, svgFilename, stepSketchSVGFile);
		storePngFile(branchName, issueId, scenarioSketchId, stepSketchSVGFile);
	}

	private void storePngFile(final String branchName, final String issueId, final String scenarioSketchId,
			final File stepSketchSVGFile) {
		try {
			final FileInputStream istream = new FileInputStream(stepSketchSVGFile);
			final ImageTagRegistry registry = ImageTagRegistry.getRegistry();
			registry.register(new PNGRegistryEntry());
			final TranscoderInput input = new TranscoderInput(istream);

			final File pngfile = new File(getStepSketchSVGDirectory(branchName, issueId, scenarioSketchId),
					"sketch.png");
			final FileOutputStream ostream = new FileOutputStream(pngfile);
			final TranscoderOutput output = new TranscoderOutput(ostream);
			transcoder.transcode(input, output);
			ostream.flush();
			ostream.close();

		} catch (final FileNotFoundException e) {
			LOGGER.error("Could not write PNG file.");
			LOGGER.error(e.toString());
		} catch (final IOException e) {
			LOGGER.error("The FileOutputStream could not be closed properly.");
			LOGGER.error(e.toString());
		} catch (final TranscoderException e) {
			LOGGER.error("Could not transcode SVG to PNG.");
			LOGGER.error(e.toString());
		}
	}

	private void storeSvgFile(final String branchName, final String issueId, final String scenarioSketchId,
			final StepSketch stepSketch, final String svgFilename, final File stepSketchSVGFile) {
		try {
			final FileWriter writer = new FileWriter(stepSketchSVGFile);
			writer.write(stepSketch.getSvgXmlString());
			writer.close();

			stepSketch.setSketchFileName(svgFilename);
			final File destinationFile = getStepSketchFile(branchName, issueId, scenarioSketchId,
					stepSketch.getStepSketchId());
			ScenarioDocuXMLFileUtil.marshal(stepSketch, destinationFile);
		} catch (final IOException e) {
			LOGGER.error("Could not write SVG file.");
			LOGGER.error(e.toString());
		}
	}

	public void copyOriginalScreenshot(final File originalScreenshot, final String branchName, final String issueId,
			final String scenarioSketchId) {
		try {
			final File destination = new File(getSVGDirectory(branchName, issueId, scenarioSketchId),
					"original.png");
			FileUtils.copyFile(originalScreenshot, destination);
		} catch (final IOException e) {
			LOGGER.error("Couldn't copy original screenshot to stepsketch!");
			e.printStackTrace();
		}
	}

}
