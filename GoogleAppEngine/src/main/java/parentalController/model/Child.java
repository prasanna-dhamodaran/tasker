package parentalController.model;

import com.google.appengine.api.datastore.Key;

import javax.jdo.annotations.*;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


@PersistenceCapable
public class Child
{
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key childId;

    @Persistent
    private String email;

    @Persistent
    private String firstName;

    @Persistent
    private String lastName;

    @Persistent
    private String token;

    @Persistent(defaultFetchGroup = "true")
    private Set<Task> tasks;

    public Child()
    {
        this.tasks = new HashSet<Task>();
    }

    public Key getChildId()
    {
        return childId;
    }

    public void setChildId(Key childId)
    {
        this.childId = childId;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    public String getLastName()
    {
        return lastName;
    }

    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }

    public String getToken()
    {
        return token;
    }

    public void setToken(String token)
    {
        this.token = token;
    }

    public Set<Task> getTasks()
    {
        return tasks;
    }

    public void setTasks(Set<Task> tasks)
    {
        this.tasks = tasks;
    }

    public boolean addTask(Task task)
    {
        Set<Task> allTasks = this.getTasks();

        allTasks.add(task);

        return true;
    }
}
