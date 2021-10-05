package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class TaskListCommunicationThreadHandler implements Runnable
{
  private DataInputStream in;
  private DataOutputStream out;
  private String ip;
  private TaskList tasks;

  public TaskListCommunicationThreadHandler(Socket socket, TaskList tasks)
      throws IOException
  {
    this.tasks = tasks;
    this.in = new DataInputStream(socket.getInputStream());
    this.out = new DataOutputStream(socket.getOutputStream());
    this.ip = socket.getInetAddress().getHostAddress();
  }

  @Override public void run()
  {
    boolean run = true;

    try
    {
      while (run)
      {
        int toDo = in.readInt();
        System.out.println("client " + ip + "> " + toDo);

        switch (toDo)
        {
          case 1:
            addTask();
            break;

          case 2:
            getTask();
            break;

          case 3:
            getSize();
            break;

          default:
            out.writeUTF("EXIT");
            System.out.println("Server> EXIT");
            System.out.println("client " + ip + " has been disconnected");
            run = false;
            break;
        }
      }
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }

  }

  private void getSize() throws IOException
  {
    out.writeInt(tasks.size());
    System.out.println("Server> " + tasks.size());
  }

  private void getTask() throws IOException
  {
    if (tasks.size() == 0)
    {
      out.writeUTF("ERROR");
      System.out.println("Server> ERROR");
    }
    else
    {
      Task task1 = tasks.getAndRemoveNextTask();
      out.writeUTF(task1.getText());
      out.writeLong(task1.getEstimatedTime());
      System.out.println(
          "Server> " + task1.getText() + ": " + task1.getEstimatedTime());
    }
  }

  private void addTask() throws IOException
  {
    String task = in.readUTF();
    System.out.println("client " + ip + "> " + task);
    int taskTime = in.readInt();
    System.out.println("client " + ip + "> " + taskTime);
    tasks.add(new Task(task, taskTime));
    out.writeUTF("Server> Task has been added");
  }
}
