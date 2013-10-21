package com.motorola.root.model;

public class Task
{
    private String taskDesc;
    private Integer taskPoints;
    private boolean taskStatus;
    private String taskId;
    private Boolean isTaskChanged;

    public Task(String taskDesc, Integer taskPoints, boolean taskStatus, String taskId, Boolean isTaskChanged)
    {
        this.taskDesc = taskDesc;
        this.taskPoints = taskPoints;
        this.taskStatus = taskStatus;
        this.taskId = taskId;
        this.isTaskChanged = isTaskChanged;
    }

    public String getTaskDesc() {
        return taskDesc;
    }

    public Integer getTaskPoints() {
        return taskPoints;
    }

    public boolean getTaskStatus() {
        return taskStatus;
    }

    public String getTaskId() {
        return taskId;
    }

    public Boolean isTaskChanged() {
        return isTaskChanged;
    }
}
