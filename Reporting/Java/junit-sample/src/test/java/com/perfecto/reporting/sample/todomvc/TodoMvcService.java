package com.perfecto.reporting.sample.todomvc;

import com.perfecto.reportium.client.ReportiumClient;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * TodoMVC service for managing todos in the application
 */
public class TodoMvcService {

    private WebDriver driver;
    private ReportiumClient reportiumClient;


    private final static long timeOutSeconds = 3;
    private final static long sleepMillis = 500;

    public TodoMvcService(WebDriver driver, ReportiumClient reportiumClient) {
        this.driver = driver;
        this.reportiumClient = reportiumClient;
    }

    /**
     * Returns the name of the created todo
     * @param prefix Basis for the enw todo name. A unique postfix is appended to the prefix.
     * @return name of the created todo
     */
    public String createUniqueTodo(String prefix, By newTodoBy) {
        String todoName = prefix + " " + UUID.randomUUID().toString();
        reportiumClient.testStep("Add new unique todo and commit with Enter key. Generated todo name is " + todoName);
        WebElement newTodo = findElement(newTodoBy);
        newTodo.sendKeys(todoName, Keys.ENTER);
        return todoName;
    }

    /**
     * Verifies that a single todo with the given name exists in the todos list
     * @param todoName Todo name
     */
    public void verifyAddedTodo(String todoName) {
        reportiumClient.testStep("Validate new todo exists in todo list");
        List<WebElement> elements = driver.findElements(getTodoBy(todoName));
        Assert.assertEquals(elements.size(), 1);
    }

    /**
     * Removes the todo with the given name from the list. Assumes todo name is unique
     * @param todoName Todo name
     */
    public void removeTodo(String todoName) {
        reportiumClient.testStep("Hover over new todo");
        List<WebElement> elements = driver.findElements(getTodoBy(todoName));
        Assert.assertEquals(elements.size(), 1);
        WebElement todo = elements.get(0);
        Actions builder = new Actions(driver);
        builder.moveToElement(todo).perform();

        reportiumClient.testStep("Remove todo by clicking on X");
        WebElement removeTodoX = todo.findElement(By.className("destroy"));
        new WebDriverWait(driver, 3).until(ExpectedConditions.visibilityOf(removeTodoX));
        // Clicking the WebElement does not work with Firefox
        builder.click(removeTodoX).perform();
    }

    /**
     * Verifies that the todo with the given name was removed from the list
     * @param todoName Todo name
     */
    public void verifyRemovedTodo(String todoName) {
        reportiumClient.testStep("Validate new todo removed from list");
        new WebDriverWait(driver, 3)
                .pollingEvery(500, TimeUnit.MILLISECONDS)
                .until(ExpectedConditions.invisibilityOfElementLocated(getTodoBy(todoName)));
    }

    /**
     * Mark todo with given name as completed
     */
    public void completeTodo(String todoName) {
        WebElement todo = driver.findElement(getTodoBy(todoName));
        reportiumClient.testStep("Mark todo " + todoName + " as completed");
        WebElement checkbox = todo.findElement(By.tagName("input"));
        checkbox.click();
    }

    /**
     * Verify todo with given name is marked as completed
     */
    public void verifyCompletedTodo(String todoName) {
        WebElement todo = driver.findElement(getTodoBy(todoName));
        reportiumClient.testStep("Mark todo " + todoName + " as completed");
        WebElement li = todo.findElement(By.xpath(".."));
        Assert.assertTrue(li.getAttribute("class").contains("completed"));
    }

    /**
     * Click on the "Completed" button to show only completed todos
     */
    public void filterCompleted() {
        reportiumClient.testStep("Filter by completed");
        WebElement completed = findElement(By.xpath("//a[@href='#/completed']"));
        completed.click();
    }

    /**
     * Returns the By object for a given todo identified by its name
     * @param name todo name
     * @return By that identifies the todo by name
     */
    private By getTodoBy(String name) {
        return By.xpath("//div[./label[text()[normalize-space(.)='" + name + "']]]");
    }

    /**
     * Replaces driver.findElement with a WebDriverWait
     *
     * @param by object describing the element to find
     * @return The element identified by the given by
     */
    protected WebElement findElement(By by) {
        return new WebDriverWait(driver, timeOutSeconds)
                .pollingEvery(sleepMillis, TimeUnit.MILLISECONDS)
                .until(ExpectedConditions.presenceOfElementLocated(by));
    }


}
