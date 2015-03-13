// Copyright 2015 ThoughtWorks, Inc.

// This file is part of getgauge/Intellij-plugin.

// getgauge/Intellij-plugin is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.

// getgauge/Intellij-plugin is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.

// You should have received a copy of the GNU General Public License
// along with getgauge/Intellij-plugin.  If not, see <http://www.gnu.org/licenses/>.

package com.thoughtworks.gauge.execution;

import com.intellij.execution.CommonProgramRunConfigurationParameters;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.application.ApplicationConfiguration;
import com.intellij.execution.application.ApplicationConfigurationType;
import com.intellij.execution.configuration.ConfigurationFactoryEx;
import com.intellij.execution.configurations.*;
import com.intellij.execution.executors.DefaultDebugExecutor;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMExternalizer;
import com.intellij.openapi.util.WriteExternalException;
import com.jgoodies.common.base.Strings;
import com.thoughtworks.gauge.GaugeConstant;
import com.thoughtworks.gauge.exception.GaugeNotFoundException;
import com.thoughtworks.gauge.util.GaugeUtil;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.thoughtworks.gauge.GaugeConstant.ENV_FLAG;
import static com.thoughtworks.gauge.GaugeConstant.GAUGE_DEBUG_OPTS_ENV;

public class GaugeRunConfiguration extends LocatableConfigurationBase implements RunProfileWithCompileBeforeLaunchOption {

    public static final String JAVA_DEBUG_PORT = "50005";
    public static final String SIMPLE_CONSOLE_FLAG = "--simple-console";
    public static final String TAGS_FLAG = "--tags";
    public static final String PARALLEL_FLAG = "--parallel";
    private static final String PARALLEL_NODES_FLAG = "--n";
    private String specsToExecute;
    private Module module;
    private String environment;
    private String tags;
    private boolean execInParallel;
    private String parallelNodes;
    public ApplicationConfiguration programParameters;

    public GaugeRunConfiguration(String name, Project project, ConfigurationFactoryEx configurationFactory) {
        super(project, configurationFactory, name);
        this.programParameters  = new ApplicationConfiguration(name, project, ApplicationConfigurationType.getInstance());
    }

    @NotNull
    @Override
    public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
        return new GaugeExecutionConfigurationSettingsEditor();
    }

    @Nullable
    @Override
    public RunProfileState getState(@NotNull Executor executor, @NotNull final ExecutionEnvironment env) throws ExecutionException {
        return new CommandLineState(env) {
            @NotNull
            @Override
            protected ProcessHandler startProcess() throws ExecutionException {
                GeneralCommandLine commandLine = new GeneralCommandLine();
                try {
                    commandLine.setExePath(GaugeUtil.getGaugeExecPath());
                } catch (GaugeNotFoundException e) {
                    commandLine.setExePath(GaugeConstant.GAUGE);
                } finally {
                    addFlags(commandLine, env);
                    return GaugeRunProcessHandler.runCommandLine(commandLine);
                }
            }
        };
    }

    private void addFlags(GeneralCommandLine commandLine, ExecutionEnvironment env) {
        commandLine.addParameter(SIMPLE_CONSOLE_FLAG);
        if (!Strings.isBlank(tags)) {
            commandLine.addParameter(TAGS_FLAG);
            commandLine.addParameter(tags);
        }
        commandLine.setWorkDirectory(getProject().getBaseDir().getPath());
        if (!Strings.isBlank(environment)) {
            commandLine.addParameters(ENV_FLAG, environment);
        }
        addParallelExecFlags(commandLine);
        addProgramArguments(commandLine);
      
        if (!Strings.isBlank(specsToExecute)) {
            addSpecs(commandLine, specsToExecute);
        }
        if (DefaultDebugExecutor.EXECUTOR_ID.equals(env.getExecutor().getId())) {
            commandLine.getEnvironment().put(GAUGE_DEBUG_OPTS_ENV, JAVA_DEBUG_PORT);
        }
        
    }

    private void addProgramArguments(GeneralCommandLine commandLine) {
        if (programParameters == null) {
            return;
        }
        String parameters = programParameters.getProgramParameters();
        if (!Strings.isEmpty(parameters)) {
            commandLine.addParameters(programParameters.getProgramParameters().split(" "));
        }
        Map<String, String> envs = programParameters.getEnvs();
        if (!envs.isEmpty()) {
            commandLine.setEnvParams(envs);
        }
        if (!programParameters.getWorkingDirectory().isEmpty()) {
            commandLine.setWorkDirectory(new File(programParameters.getWorkingDirectory()));
        }
    }

    private void addParallelExecFlags(GeneralCommandLine commandLine) {
        if (execInParallel) {
            commandLine.addParameter(PARALLEL_FLAG);
            try {
                if (!Strings.isEmpty(parallelNodes)) {
                    int nodes = Integer.parseInt(this.parallelNodes);
                    commandLine.addParameters(PARALLEL_NODES_FLAG, parallelNodes);
                }
            } catch (NumberFormatException e) {
                System.err.println("Incorrect number of parallel execution streams specified: " + parallelNodes);
                e.printStackTrace();
            }
        }
    }

    private void addSpecs(GeneralCommandLine commandLine, String specsToExecute) {
        String[] specNames = specsToExecute.split(",");
        for (String specName : specNames) {
            if (!specName.isEmpty()) {
                commandLine.addParameter(specName.trim());
            }
        }
    }

    @Override
    public void readExternal(Element element) throws InvalidDataException {
        super.readExternal(element);
        environment = JDOMExternalizer.readString(element, "environment");
        specsToExecute = JDOMExternalizer.readString(element, "specsToExecute");
        tags = JDOMExternalizer.readString(element, "tags");
        parallelNodes = JDOMExternalizer.readString(element, "parallelNodes");
        execInParallel = JDOMExternalizer.readBoolean(element, "execInParallel");
        programParameters.setProgramParameters(JDOMExternalizer.readString(element, "programParameters"));
        programParameters.setWorkingDirectory(JDOMExternalizer.readString(element, "workingDirectory"));
        HashMap<String, String> envMap = new HashMap<String, String>();
        JDOMExternalizer.readMap(element, envMap, "envMap", "envMap" );
        programParameters.setEnvs(envMap);
    }

    @Override
    public void writeExternal(Element element) throws WriteExternalException {
        super.writeExternal(element);
        JDOMExternalizer.write(element, "environment", environment);
        JDOMExternalizer.write(element, "specsToExecute", specsToExecute);
        JDOMExternalizer.write(element, "tags", tags);
        JDOMExternalizer.write(element, "parallelNodes", parallelNodes);
        JDOMExternalizer.write(element, "execInParallel", execInParallel);
        JDOMExternalizer.write(element, "programParameters", programParameters.getProgramParameters());
        JDOMExternalizer.write(element, "workingDirectory", programParameters.getWorkingDirectory());
        JDOMExternalizer.writeMap(element, programParameters.getEnvs(), "envMap", "envMap");
    }

    @NotNull
    @Override
    public Module[] getModules() {
        return ModuleManager.getInstance(getProject()).getModules();
    }

    public void setSpecsToExecute(String specsToExecute) {
        this.specsToExecute = specsToExecute;
    }

    public String getSpecsToExecute() {
        return specsToExecute;
    }


    public void setModule(Module module) {
        this.module = module;
    }

    public Module getModule() {
        return module;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public void setSpecsArrayToExecute(ArrayList<String> specsArrayToExecute) {
        StringBuilder builder = new StringBuilder("");
        for (String specName : specsArrayToExecute) {
            builder.append(specName);
            if (specsArrayToExecute.indexOf(specName) != specsArrayToExecute.size() - 1) {
                builder.append(",");
            }
        }
        setSpecsToExecute(builder.toString());
    }

    public void setExecInParallel(boolean execInParallel) {
        this.execInParallel = execInParallel;
    }

    public boolean getExecInParallel() {
        return execInParallel;
    }

    public void setParallelNodes(String parallelNodes) {
        this.parallelNodes = parallelNodes;
    }

    public String getParallelNodes() {
        return parallelNodes;
    }

    public CommonProgramRunConfigurationParameters getProgramParameters() {
        return programParameters;
    }
}
