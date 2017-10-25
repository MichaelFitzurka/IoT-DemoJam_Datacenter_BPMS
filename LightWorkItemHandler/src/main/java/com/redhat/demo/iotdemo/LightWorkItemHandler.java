package com.redhat.demo.iotdemo;

import java.io.IOException;

import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

public class LightWorkItemHandler implements WorkItemHandler {

    private static final String DATA_GRID_URL_PREFIX = "http://iotdj-datacenter:30080/rest/default/";
    private static final String ISSUE_SOLVED = "solved";
    private static final String PARAM_DEVICE_ID = "deviceID";
    private static final String PARAM_DEVICE_TYPE = "deviceType";
    private static final String PRODUCER_URL_PATTERN =
            "http://iotdj-datacenter:30711/lightsOff?deviceType=%s&deviceID=%s";

    HttpProducer producer = null;
    DataGridHelper dgHelper = null;

    public LightWorkItemHandler() {
        super();
    }

    public void abortWorkItem(final WorkItem workItem, final WorkItemManager manager) {
        System.out.println("Oh no, my item aborted..");
    }

    public void executeWorkItem(final WorkItem workItem, final WorkItemManager manager) {
        producer = new HttpProducer();
        dgHelper = new DataGridHelper();

        String deviceType = (String) workItem.getParameter(PARAM_DEVICE_TYPE);
        String deviceID = (String) workItem.getParameter(PARAM_DEVICE_ID);

        turnAlarmLightOff(deviceType, deviceID);
        clearCachedIssue(deviceType, deviceID);

        manager.completeWorkItem(workItem.getId(), null);
    }

    private void clearCachedIssue(final String deviceType, final String deviceID) {
        System.out.println("Clearing this event in distributed cache!");

        try {
            dgHelper.putMethod(DATA_GRID_URL_PREFIX + deviceType + deviceID, ISSUE_SOLVED);
            System.out.println(String.format("Value via REST after put = <%s>",
        	    dgHelper.getMethod(DATA_GRID_URL_PREFIX + deviceType + deviceID)));
        } catch (IOException e) {
            System.out.println("Exception when calling Data Grid: " + e.getMessage());
            e.printStackTrace(System.out);
        }
    }

    private void turnAlarmLightOff(final String deviceType, final String deviceID) {
        System.out.println("Turning off alarm light!");

        try {
            producer.sendGet(String.format(PRODUCER_URL_PATTERN, deviceType, deviceID));
        } catch (Exception e) {
            System.out.println("Exception when calling Producer: " + e.getMessage());
            e.printStackTrace(System.out);
        }
    }

    @Override
    public String toString() {
        return "LightWorkItemHandler []";
    }

}
