package org.ei.drishti.domain;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class TimelineEventTest {

    private HashMap<String, String> detailsWithData;
    private Map<String, String> detailsWithoutData;

    @Before
    public void setUp() throws Exception {
        detailsWithData = new HashMap<String, String>();
        detailsWithData.put("bpSystolic", "120");
        detailsWithData.put("bpDiastolic", "80");
        detailsWithData.put("temperature", "98");
        detailsWithData.put("weight", "48");
        detailsWithData.put("hbLevel", "11");
        detailsWithData.put("motherTemperature", "98");
        detailsWithData.put("childTemperature", "98");
        detailsWithData.put("childWeight", "4");

        detailsWithoutData = new HashMap<String, String>();
    }

    @Test
    public void shouldCreateTimelineEventForANCVisitWithDetails() throws Exception {
        TimelineEvent timelineEvent = TimelineEvent.forANCCareProvided("CASE A", "1", "2012-01-01", detailsWithData);

        assertTrue(timelineEvent.detail1().contains("BP: 120/80"));
        assertTrue(timelineEvent.detail1().contains("Temp: 98 °F"));
        assertTrue(timelineEvent.detail1().contains("Weight: 48 kg"));
        assertTrue(timelineEvent.detail1().contains("Hb Level: 11"));
    }

    @Test
    public void shouldCreateTimelineEventForANCVisitExcludingThoseDetailsWhichDoNotHaveValue() throws Exception {
        TimelineEvent timelineEvent = TimelineEvent.forANCCareProvided("CASE A", "1", "2012-01-01", detailsWithoutData);

        assertFalse(timelineEvent.detail1().contains("BP:"));
        assertFalse(timelineEvent.detail1().contains("Temp:"));
        assertFalse(timelineEvent.detail1().contains("Weight:"));
        assertFalse(timelineEvent.detail1().contains("Hb Level:"));
    }

    @Test
    public void shouldCreateTimelineEventForMotherPNCVisitWithDetails() throws Exception {
        TimelineEvent timelineEvent = TimelineEvent.forMotherPNCVisit("CASE A", "1", "2012-01-01", detailsWithData);

        assertTrue(timelineEvent.detail1().contains("BP: 120/80"));
        assertTrue(timelineEvent.detail1().contains("Temp: 98 °F"));
        assertTrue(timelineEvent.detail1().contains("Hb Level: 11"));
    }

    @Test
    public void shouldCreateTimelineEventForMotherPNCVisitExcludingThoseDetailsWhichDoNotHaveValue() throws Exception {
        TimelineEvent timelineEvent = TimelineEvent.forMotherPNCVisit("CASE A", "1", "2012-01-01", detailsWithoutData);

        assertFalse(timelineEvent.detail1().contains("BP:"));
        assertFalse(timelineEvent.detail1().contains("Temp:"));
        assertFalse(timelineEvent.detail1().contains("Hb Level:"));
    }

    @Test
    public void shouldCreateTimelineEventForChildPNCVisitWithDetails() throws Exception {
        TimelineEvent timelineEvent = TimelineEvent.forChildPNCVisit("CASE A", "1", "2012-01-01", detailsWithData);

        assertTrue(timelineEvent.detail1().contains("Temp: 98 °F"));
        assertTrue(timelineEvent.detail1().contains("Weight: 4 kg"));
    }

    @Test
    public void shouldCreateTimelineEventForChildPNCVisitExcludingThoseDetailsWhichDoNotHaveValue() throws Exception {
        TimelineEvent timelineEvent = TimelineEvent.forChildPNCVisit("CASE A", "1", "2012-01-01", detailsWithoutData);

        assertFalse(timelineEvent.detail1().contains("Temp:"));
        assertFalse(timelineEvent.detail1().contains("Weight:"));
    }
}