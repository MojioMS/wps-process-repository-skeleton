package org.n52.wps.project.riesgos.quakeml.algorithm;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.n52.wps.io.data.IData;
import org.n52.wps.io.data.binding.complex.GenericFileDataBinding;
import org.n52.wps.server.AbstractObservableAlgorithm;
import org.n52.wps.server.ExceptionReport;
import org.n52.wps.server.ProcessDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.opengis.wps.x100.ProcessDescriptionsDocument;
import org.n52.wps.project.riesgos.quakeml.io.QuakeMLDataBinding;
import org.n52.wps.project.riesgos.quakeml.io.QuakeMLParser;

public class QuakeMLAlgorithm extends AbstractObservableAlgorithm {

    private static Logger LOGGER = LoggerFactory
            .getLogger(QuakeMLAlgorithm.class);

    private List<String> errors = new ArrayList<>();

    private String processID;
    private String outputID = "shakemap";

    public QuakeMLAlgorithm(){
    }

    public QuakeMLAlgorithm(String processID) {
        this.processID = processID;
    }

    @Override
    public List<String> getErrors() {
        return errors ;
    }

    @Override
    public Class<?> getInputDataType(String id) {
        return GenericFileDataBinding.class;
    }

    @Override
    public Class<?> getOutputDataType(String id) {
        return QuakeMLDataBinding.class;
    }

    @Override
    public Map<String, IData> run(Map<String, List<IData>> inputs) throws ExceptionReport {
        LOGGER.info("Starting process with id: " + processID);

        InputStream in = getClass().getResourceAsStream("QuakeML process output.xml");

        QuakeMLDataBinding quakemlDataBinding;

        QuakeMLParser theParser = new QuakeMLParser();

        try {
            quakemlDataBinding = theParser.parse(in, null, null);
        } catch (Exception e) {
            LOGGER.error("Could not parse QuakeML.", e);
            throw new ExceptionReport("Could not parse QuakeML.", ExceptionReport.NO_APPLICABLE_CODE, e);
        }

        Map<String, IData> outputMap = new HashMap<String, IData>(1);

        outputMap.put(outputID, quakemlDataBinding);

        LOGGER.info("Finished process with id: " + processID);

        return outputMap;
    }

    @Override
    public ProcessDescription getDescription() {

        try {
            InputStream in = getClass().getResourceAsStream("ShakemapAlgorithm.xml");

            ProcessDescriptionsDocument processDescriptionsDocument = ProcessDescriptionsDocument.Factory.parse(in);

            ProcessDescription processDescription = new ProcessDescription();

            processDescription.addProcessDescriptionForVersion(processDescriptionsDocument.getProcessDescriptions().getProcessDescriptionArray(0), "1.0.0");

            return processDescription;

        } catch (Exception e) {
            LOGGER.error("Could not parse ProcessDescription. Returning empty ProcessDescription.");
        }

        return new ProcessDescription();
    }

}
