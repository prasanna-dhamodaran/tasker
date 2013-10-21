package parentalController.model;


import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@PersistenceCapable
public class Task
{
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key taskId;

    @Persistent
    private String taskDescription;

    @Persistent
    private Boolean status;

    @Persistent
    private Integer points;

    private static int taskCounter = 0;

    public Task()
    {
        /*taskCounter++;
        Key taskId = KeyFactory.createKey(Task.class.getSimpleName(), taskCounter);
        this.setTaskId(taskId);*/
    }

    public Key getTaskId()
    {
        return taskId;
    }

    public void setTaskId(Key taskId)
    {
        this.taskId = taskId;
    }

    public String getTaskDescription()
    {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription)
    {
        this.taskDescription = taskDescription;
    }

    public Boolean getStatus()
    {
        return status;
    }

    public void setStatus(Boolean status)
    {
        this.status = status;
    }

    public Integer getPoints()
    {
        return points;
    }

    public void setPoints(Integer points)
    {
        this.points = points;
    }
}
