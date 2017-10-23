/*
Name: YOUR_NAME_HERE
Student number: A0111277M
Is this a group submission: no 

If it is a group submission:
Name of 2nd group member: THE_OTHER_NAME_HERE_PLEASE
Student number of 2nd group member: THE_OTHER_NO

*/
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.file.*;
import java.io.*;
import java.util.*;

public class WebServer {
    private ServerSocket socket;
    public static void main(String[] args) {
        // dummy value that is overwritten below
        int port = 8080;
        try {
          port = Integer.parseInt(args[0]);
        } catch (Exception e) {
          System.out.println("Usage: java webserver <port> ");
          System.exit(0);
        }

        WebServer serverInstance = new WebServer();
        serverInstance.start(port);
    }

    private void print(String text) {
      System.out.println(text);
    }

    private void start(int port) {
      System.out.println("Starting server on port " + port);
      // START_HERE

      // Please DO NOT copy from the Internet (or anywhere else)
      // Instead, if you see nice code somewhere try to understand it.
      //
      // After understanding the code, put it away, do not look at it,
      // and write your own code.
      // Subsequent exercises will build on the knowledge that
      // you gain during this exercise. Possibly also the exam.
      //
      // We will check for plagiarism. Please be extra careful and
      // do not share solutions with your friends.
      //
      // Good practices include
      // (1) Discussion of general approaches to solve the problem
      //     excluding detailed design discussions and code reviews.
      // (2) Hints about which classes to use
      // (3) High level UML diagrams
      //
      // Bad practices include (but are not limited to)
      // (1) Passing your solution to your friends
      // (2) Uploading your solution to the Internet including
      //     public repositories
      // (3) Passing almost complete skeleton codes to your friends
      // (4) Coding the solution for your friend
      // (5) Sharing the screen with a friend during coding
      // (6) Sharing notes
      //
      // If you want to solve this assignment in a group,
      // you are free to do so, but declare it as group work above.

      // NEEDS IMPLEMENTATION
      // You have to understand how sockets work and how to program
      // them in Java.
      // A good starting point is the socket tutorial from Oracle
      // http://docs.oracle.com/javase/tutorial/networking/sockets/
      // But there are a billion other resources on the Internet.
      //
      // Hints
      // 1. You should set up the socket(s) and then call handleClientSocket.

      try {
        socket = new ServerSocket(port);  
      } catch (IOException e) {
        print("Error creating socket: " + e);
      }
      
      while (true) {
        try {
          Socket connectionSocket = socket.accept();
          handleClientSocket(connectionSocket);
        } catch (IOException e) {
          print("Error accepting incoming socket: " + e);
        }
      }
    }

    /**
     * Handles requests sent by a client
     * @param  client Socket that handles the client connection
     */
    private void handleClientSocket(Socket client) {
      // NEEDS IMPLEMENTATION
      // This function is supposed to handle the request
      // Things to do:
      // (1) Read the request from the socket 
      // (2) Parse the request and set variables of 
      //     the HttpRequest class (at the end of the file!)
      // (3) Form a response using formHttpResponse.
      // (4) Send a response using sendHttpResponse.
      //
      // A BufferedReader might be useful here, but you can also
      // solve this in many other ways.
      try {
        client.setSoTimeout(2 * 1000);
        InputStream is = client.getInputStream();
        HttpRequest hr = new HttpRequest(is); 
        System.out.println("Created request");
        byte[] response = formHttpResponse(hr);
        System.out.println("Created response");
        sendHttpResponse(client, response);
        try {
          if (hr.getVersion().equals("HTTP/1.0")) {
            client.close();
            print("Connection closed");
          } else {
            if (hr.getKeepAlive()) {
              print("Kept alive");
            } else {
              client.close();
              print("Connection closed");
            }
          } 
        } catch (Exception e) {
          client.close();
          print("Connection closed");
        }

      } catch (IOException e) {
        print("Unable to create I/O Streams: " + e);
      }
      
    }

    /**
     * Sends a response back to the client
     * @param  client Socket that handles the client connection
     * @param  response the response that should be send to the client
     */
    private void sendHttpResponse(Socket client, byte[] response) {
      // NEEDS IMPLEMENTATION
      System.out.println("Here I'm supposed to send the response.");
      try {
        OutputStream os = client.getOutputStream();
        os.write(response);
        os.flush();
      } catch (Exception e) {
        print(e + " Unable to send response.");
      }
    }

    /**
     * Form a response to an HttpRequest
     * @param  request the HTTP request
     * @return a byte[] that contains the data that should be send to the client
     */
    private byte[] formHttpResponse(HttpRequest request) {
      // NEEDS IMPLEMENTATION
      // Make sure you follow the (modified) HTTP specification
      // in the assignment regarding header fields and newlines
      // You might want to use the concatenate method,
      // but you do not have to.
      // If you want to you can use a StringBuilder here
      // but it is possible to solve this in multiple different ways.
      byte[] response, resFileBytes;
      try {
        resFileBytes = Files.readAllBytes(Paths.get("." + request.getFilePath()));
        // print(new String(resFileBytes, "UTF-8"));  
      } catch (Exception e) {
        resFileBytes = null;
        // go to 404
      }
      if (resFileBytes != null) {
        // attach initial http header
        String statusLine = request.getVersion() + " 200 OK\r\n";
        response = statusLine.getBytes();

        String entityHeader = "Content-Length: " + resFileBytes.length + "\r\n\r\n";
        response = concatenate(response, entityHeader.getBytes());

        response = concatenate(response, resFileBytes);
        return response;
      } else {
        return form404Response(request);
      }
    }

    /**
     * Form a 404 response for a HttpRequest
     * @param  request a HTTP request
     * @return a byte[] that contains the data that should be send to the client
     */
    private byte[] form404Response(HttpRequest request) {
        // NEEDS IMPLEMENTATION
        // This should return a 404 response
        // You should use it where appropriate
        // To get the content of the 404 response
        // call get404Content.
        // If you want to you can use a StringBuilder here
        // but it is possible to do this in multiple different ways.
      byte[] response;
      String statusLine = request.getVersion() + " 404 Not Found\r\n";
      String _404Content = get404Content(request.getFilePath());
      String entityHeader = "Content-Length: " + _404Content.length() + "\r\n\r\n";

      response = statusLine.getBytes();
      response = concatenate(response, entityHeader.getBytes());
      response = concatenate(response, _404Content.getBytes());
      return response;
    }
    

    /**
     * Concatenates 2 byte[] into a single byte[]
     * This is a function provided for your convenience.
     * @param  buffer1 a byte array
     * @param  buffer2 another byte array
     * @return concatenation of the 2 buffers
     */
    private byte[] concatenate(byte[] buffer1, byte[] buffer2) {
      byte[] returnBuffer = new byte[buffer1.length + buffer2.length];
      System.arraycopy(buffer1, 0, returnBuffer, 0, buffer1.length);
      System.arraycopy(buffer2, 0, returnBuffer, buffer1.length, buffer2.length);
      return returnBuffer;
    }

    /**
     * Returns a string that represents a 404 error
     * You should use this string as the return website
     * for 404 errors.
     * @param  filePath path of the file that caused the 404
     * @return a String that represents a 404 error website
     */
    private String get404Content(String filePath) {
      // You should not change this function. Use it as it is.
      StringBuilder sb = new StringBuilder();
      sb.append("<html>");
      sb.append("<head>");
      sb.append("<title>");
      sb.append("404 Not Found");
      sb.append("</title>");
      sb.append("</head>");
      sb.append("<body>");
      sb.append("<h1>404 Not Found</h1> ");
      sb.append("<p>The requested URL <i>" + filePath + "</i> was not found on this server</p>");
      sb.append("</body>");
      sb.append("</html>");

      return sb.toString();
    }
}



class HttpRequest {
    // NEEDS IMPLEMENTATION
    // This class should represent a HTTP request.
    // Feel free to add more attributes if needed.
    private String filePath, reqType, version, header;
    private boolean keepAlive;
    private HashMap<String, String> headers;
    private BufferedReader br;

    public HttpRequest(InputStream is) {
      br = new BufferedReader(new InputStreamReader(is));
      headers = new HashMap<String, String>();
      try {
        System.out.println("parsing query line");
        String query = br.readLine();
        parseRequest(query);

        System.out.println("parsing headers");
        while ((header = br.readLine()) != null) {
          if (header.length() <= 0) {
            break;
          }
          String key, value;
          key = header.split(":")[0];
          value = header.split(":")[1].substring(1); // remove empty space
          System.out.println("KEY:" + key + "value:" + value);
          if (header.equals("")) {
            break;
          }
          headers.put(key, value);
        }
        System.out.println(headers.get("Connection"));
        if (headers.get("Connection") != null && headers.get("Connection").equals("keep-alive")) {
          keepAlive = true;
        }
        System.out.println("Got out of this loop");

      } catch (IOException e) {
        System.out.println("Unable to read line to process query: " + e);
      }
    }

    String getFilePath() {
      return filePath;
    }

    String getReqType() {
      return reqType;
    }

    String getVersion() {
      return version;
    }

    boolean getKeepAlive() {
      return keepAlive;
    }

    void parseRequest(String query) {
      reqType = query.split(" ")[0];
      filePath = query.split(" ")[1];
      version = query.split(" ")[2];

      System.out.println("reqType: " + reqType + " filePath: " + filePath);
    }
    // NEEDS IMPLEMENTATION
    // If you add more private variables, add you getter methods here
}