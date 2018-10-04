/*
 * Copyright (C) 2007 - 2017 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 as published
 * by the Free Software Foundation.
 *
 * If the program is linked with libraries which are licensed under one of
 * the following licenses, the combination of the program with the linked
 * library is not considered a "derivative work" of the program:
 *
 *       • Apache License, version 2.0
 *       • Apache Software License, version 1.0
 *       • GNU Lesser General Public License, version 3
 *       • Mozilla Public License, versions 1.0, 1.1 and 2.0
 *       • Common Development and Distribution License (CDDL), version 1.0
 *
 * Therefore the distribution of the program linked with libraries licensed
 * under the aforementioned licenses, is permitted by the copyright holders
 * if the distribution is compliant with both the GNU General Public
 * License version 2 and the aforementioned licenses.
 *
 * As an exception to the terms of the GPL, you may copy, modify,
 * propagate, and distribute a work formed by combining 52°North WPS
 * GeoTools Modules with the Eclipse Libraries, or a work derivative of
 * such a combination, even if such copying, modification, propagation, or
 * distribution would otherwise violate the terms of the GPL. Nothing in
 * this exception exempts you from complying with the GPL in all respects
 * for all of the code used other than the Eclipse Libraries. You may
 * include this exception and its grant of permissions when you distribute
 * 52°North WPS GeoTools Modules. Inclusion of this notice with such a
 * distribution constitutes a grant of such permissions. If you do not wish
 * to grant these permissions, remove this paragraph from your
 * distribution. "52°North WPS GeoTools Modules" means the 52°North WPS
 * modules using GeoTools functionality - software licensed under version 2
 * or any later version of the GPL, or a work based on such software and
 * licensed under the GPL. "Eclipse Libraries" means Eclipse Modeling
 * Framework Project and XML Schema Definition software distributed by the
 * Eclipse Foundation and licensed under the Eclipse Public License Version
 * 1.0 ("EPL"), or a work based on such software and licensed under the EPL.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 */
package org.n52.wps.project.riesgos.shakemap;

import java.io.IOException;
import java.io.InputStream;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
//import org.geotools.geojson.feature.FeatureJSON;

import org.junit.Assert;
import org.junit.Test;
import org.n52.wps.io.test.datahandler.AbstractTestCase;
import org.n52.wps.project.riesgos.shakemap.io.QuakeMLDataBinding;
import org.n52.wps.project.riesgos.shakemap.io.QuakeMLGenerator;
import org.n52.wps.project.riesgos.shakemap.io.QuakeMLParser;
import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test class for GeoJSON parser and generator
 *
 * @author Maurin Radtke <m.radtke@52north.org>
 *
 */
public class QuakeMLGeneratorTest extends AbstractTestCase<QuakeMLParser> {

    protected static final Logger LOGGER = LoggerFactory.getLogger(QuakeMLGeneratorTest.class);

    @Test
    public void testGenerateInvalidQuakeML() {

        QuakeMLParser theParser = new QuakeMLParser();

        QuakeMLGenerator theGenerator = new QuakeMLGenerator();

        InputStream in = getClass().getResourceAsStream("QuakeML process output_wrong.xml");

        QuakeMLDataBinding theBinding = theParser.parse(in, null, null);

        try {
            in.close();
        } catch (IOException e) {
            LOGGER.info("Failed to close InputStream.", e);
        }

        InputStream inGenerated = null;

        try {
            inGenerated = theGenerator.generateStream(theBinding, null, null);
        } catch (IOException ex) {
            Assert.fail("Could not generate Inputstream. " + ex);
        }

        QuakeMLDataBinding generatedBinding = theParser.parse(inGenerated, null, null);
        try {
            inGenerated.close();
        } catch (IOException e) {
            LOGGER.info("Failed to close InputStream.", e);
        }

        Assert.assertTrue(generatedBinding.getPayload() != null);

        FeatureCollection fc = generatedBinding.getPayload();

        FeatureIterator features = fc.features();

        Feature firstFeature = null;
        Feature secondFeature = null;
        Feature thirdFeature = null;

        while (features.hasNext()) {
            Feature currentFeature = features.next();
            switch (currentFeature.getIdentifier().getID()) {
                case "65570":
                    firstFeature = currentFeature;
                    break;
                case "95564":
                    secondFeature = currentFeature;
                    break;
                case "334079":
                    thirdFeature = currentFeature;
                    break;
            }
        }

        Property magnitudeMagValue = firstFeature.getProperty("magnitude.mag.value");

        Assert.assertTrue(magnitudeMagValue.getValue().toString().equals("7.4"));

        Property time = secondFeature.getProperty("origin.time.value");

        Assert.assertTrue(
                time.getValue().toString().equals("65303-01-01T00:00:00.000000Z"));

        Property depthValue = secondFeature.getProperty("origin.depth.value");

        Assert.assertTrue(
                depthValue.getValue().toString().equals("8.0"));

        Assert.assertTrue(thirdFeature.getIdentifier().getID().equals("334079"));

        Property originUncertaintyAzimuthMaxHorizontalUncertainty = thirdFeature.getProperty("originUncertainty.azimuthMaxHorizontalUncertainty");

        Assert.assertTrue(
                originUncertaintyAzimuthMaxHorizontalUncertainty.getValue().toString().equals("nan"));

    }

    @Test
    public void testGenerateValidQuakeML() {

        QuakeMLParser theParser = new QuakeMLParser();

        QuakeMLGenerator theGenerator = new QuakeMLGenerator();

        InputStream in = getClass().getResourceAsStream("QuakeML process output_right.xml");

        QuakeMLDataBinding theBinding = theParser.parse(in, null, null);

        try {
            in.close();
        } catch (IOException e) {
            LOGGER.info("Failed to close InputStream.", e);
        }

        InputStream inGenerated = null;

        try {
            inGenerated = theGenerator.generateStream(theBinding, null, null);
        } catch (IOException ex) {
            Assert.fail("Could not generate Inputstream. " + ex);
        }

        QuakeMLDataBinding generatedBinding = theParser.parse(inGenerated, null, null);
        try {
            inGenerated.close();
        } catch (IOException e) {
            LOGGER.info("Failed to close InputStream.", e);
        }

        Assert.assertTrue(theBinding.getPayload() != null);

        FeatureCollection fc = theBinding.getPayload();

        Feature firstFeature =  fc.features().next();

        Assert.assertTrue(firstFeature.getIdentifier().getID().equals("smi:nz.org.geonet/event/2806038g"));

    }

    @Override
    protected void initializeDataHandler() {
        dataHandler = new QuakeMLParser();
    }

}
