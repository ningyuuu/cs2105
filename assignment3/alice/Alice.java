// Author: A0111277M Ning Yu

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.EOFException;
import java.io.FileNotFoundException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.PublicKey;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.KeyGenerator;
import javax.crypto.SealedObject;

/**********************************************************************
  * This skeleton program is prepared for weak and average students.  *
  *                                                                   *
  * If you are very strong in programming, DIY!                       *
  *                                                                   *
  * Feel free to modify this program.                                 *
  *********************************************************************/

// Alice knows Bob's public key
// Alice sends Bob session (AES) key
// Alice receives messages from Bob, decrypts and saves them to file

class Alice { // Alice is a TCP  client
    
    private ObjectOutputStream toBob;   // to send session key to Bob
    private ObjectInputStream fromBob;  // to read encrypted messages from Bob
    private Crypto crypto;        // object for encryption and decryption
    public static final String MESSAGE_FILE = "msgs.txt"; // file to store messages
    
    public static void main(String[] args) {
        
        // Check if the number of command line argument is 2
        if (args.length != 2) {
            System.err.println("Usage: java Alice BobIP BobPort");
            System.exit(1);
        }
        
        new Alice(args[0], args[1]);
    }
    
    // Constructor
    public Alice(String ipStr, String portStr) {
        
        this.crypto = new Crypto();
        Socket socket = null;
        try {
            socket = new Socket(ipStr, Integer.parseInt(portStr));
            toBob = new ObjectOutputStream(socket.getOutputStream());
            fromBob = new ObjectInputStream(socket.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        
        // Send session key to Bob
        // System.out.println("SENDING SESSION KEY");
        sendSessionKey();
        
        // Receive encrypted messages from Bob,
        // decrypt and save them to file
        receiveMessages();

        while (true) {
            // wait for bob to close before exiting
            try {
                toBob.write(1);
            } catch (SocketException e) {
                // System.out.println("CAUGHT THE ERROR");
                // e.printStackTrace();
                System.exit(1);
            } catch (Exception e) {
                // System.out.println("NORMAL EXCEPTION");
                System.exit(1);
            }
        }
    }
    
    // Send session key to Bob
    public void sendSessionKey() {
        SealedObject sessionKey = crypto.getSessionKey();
        try {
            this.toBob.writeObject(sessionKey);       
        } catch (IOException e) {
            // e.printStackTrace();
            // System.out.println("SOCKET CLOSED AT OTHER SIDE.");
            System.exit(1);
        }
        
    }
    
    // Receive messages one by one from Bob, decrypt and write to file
    public void receiveMessages() {

        // How to detect Bob has no more data to send?
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(MESSAGE_FILE);    
        } catch (Exception e) {
            e.printStackTrace();
        }

        int numBytes;
        SealedObject received;

        while (true) {
            try {
                // System.out.println("RECEVING");
                received = (SealedObject) fromBob.readObject();
                fos.write((crypto.decryptMessage(received)+"\n").getBytes());
            } catch (EOFException eofe) {
                break;
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        } 
    }
    
    /*****************/
    /** inner class **/
    /*****************/
    class Crypto {
        
        // Bob's public key, to be read from file
        private PublicKey pubKey;
        // Alice generates a new session key for each communication session
        private SecretKey sessionKey;
        // File that contains Bob' public key
        public static final String PUBLIC_KEY_FILE = "public.key";
        
        // Constructor
        public Crypto() {
            // Read Bob's public key from file
            readPublicKey();
            // Generate session key dynamically
            initSessionKey();
        }
        
        // Read Bob's public key from file
        public void readPublicKey() {
            // key is stored as an object and need to be read using ObjectInputStream.
            // See how Bob read his private key as an example.
            File pubKeyFile = new File(PUBLIC_KEY_FILE);
            try {
                ObjectInputStream ois = new ObjectInputStream(
                    new FileInputStream(PUBLIC_KEY_FILE)
                );
                this.pubKey = (PublicKey) ois.readObject();
                ois.close();
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
        
        // Generate a session key
        public void initSessionKey() {
            // suggested AES key length is 128 bits
            KeyGenerator keyGen = null;
            try {
                keyGen = KeyGenerator.getInstance("AES");
            } catch (NoSuchAlgorithmException nsae) {
                System.out.println("No such algo: AES");
                nsae.printStackTrace();
                System.exit(1);
            }
            keyGen.init(128);
            this.sessionKey = keyGen.generateKey();
        }
        
        // Seal session key with RSA public key in a SealedObject and return
        public SealedObject getSessionKey() {
            
            // Alice must use the same RSA key/transformation as Bob specified
            Cipher cipher = null;
            try {
                cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
                cipher.init(cipher.ENCRYPT_MODE, pubKey);
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
            // RSA imposes size restriction on the object being encrypted (117 bytes).
            // Instead of sealing a Key object which is way over the size restriction,
            // we shall encrypt AES key in its byte format (using getEncoded() method).
            byte[] sessionKeyBytes = sessionKey.getEncoded();
            SealedObject sealedSessKey = null;
            try {
                sealedSessKey = new SealedObject(sessionKeyBytes, cipher);
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
            return sealedSessKey;
        }   
        
        // Decrypt and extract a message from SealedObject
        public String decryptMessage(SealedObject encryptedMsgObject) {
            
            String decryptedMessage = null;
            // Alice and Bob use the same AES key/transformation
            try {
                Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
                cipher.init(Cipher.DECRYPT_MODE, sessionKey);
                decryptedMessage = (String)encryptedMsgObject.getObject(cipher);    
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
            return decryptedMessage;
            
        }
    }
}