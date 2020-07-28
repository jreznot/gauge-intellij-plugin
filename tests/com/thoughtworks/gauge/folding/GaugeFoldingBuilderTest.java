/*
 * Copyright (C) 2020 ThoughtWorks, Inc.
 *  
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  
 *        http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.thoughtworks.gauge.folding;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.util.TextRange;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GaugeFoldingBuilderTest {

    private ASTNode heading;
    private ASTNode node;
    private List<FoldingDescriptor> descriptors;

    @Before
    public void setUp() {
        descriptors = new ArrayList<>();
        node = mock(ASTNode.class);
        heading = mock(ASTNode.class);
        when(node.getStartOffset()).thenReturn(0);
        when(heading.getText()).thenReturn("## heading\n");
    }

    @Test
    public void testShouldAddNode() {
        when(node.getText()).thenReturn("## heading\n* text\n* text1");

        new SpecFoldingBuilder().addNode(descriptors, node, heading);

        assertEquals(descriptors.size(), 1);
        TextRange range = descriptors.get(0).getRange();
        String message = String.format("Expected: %s, Actual: %s", range.toString(), "(11,25)");
        assertTrue(message, range.equalsToRange(11, 25));
    }

    @Test
    public void testShouldAddNodeWithMultipleNewLinesAtTheEnd() {
        when(node.getText()).thenReturn("## heading\n* text\n* text1\n\n\n");

        new SpecFoldingBuilder().addNode(descriptors, node, heading);

        assertEquals(descriptors.size(), 1);
        TextRange range = descriptors.get(0).getRange();
        String message = String.format("Expected: %s, Actual: %s", range.toString(), "(11,26)");
        assertTrue(message, range.equalsToRange(11, 26));
    }

    @Test
    public void testShouldAddNodeEndingWithNewLine() {
        when(node.getText()).thenReturn("## heading\n* step 1\n* step 2\n");

        new SpecFoldingBuilder().addNode(descriptors, node, heading);

        assertEquals(descriptors.size(), 1);
        TextRange range = descriptors.get(0).getRange();
        String message = String.format("Expected: %s, Actual: %s", range.toString(), "(11,28)");
        assertTrue(message, range.equalsToRange(11, 28));
    }

    @Test
    public void testShouldNotAddNodeWhenOnlyHeadingIsPresent() {
        when(node.getText()).thenReturn("## heading\n\n");

        new SpecFoldingBuilder().addNode(descriptors, node, heading);

        assertEquals(descriptors.size(), 0);
    }

    @Test
    public void testShouldNotAddNodeWhenHeadingNotPresent() {
        when(node.getText()).thenReturn("## heading\n");

        new SpecFoldingBuilder().addNode(descriptors, node, null);

        assertEquals(descriptors.size(), 0);
    }

    @Test
    public void testShouldNotAddNode() {
        when(node.getText()).thenReturn("## head\n");

        new SpecFoldingBuilder().addNode(descriptors, node, heading);

        assertEquals(descriptors.size(), 0);
    }
}