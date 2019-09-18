package servidorarchivos;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;


public class Servidor {

    private int puerto;
    private Socket Socket;

    public Servidor(String[] args) {
        puerto = Integer.valueOf(args[0]);
        Socket = null;
    }

    public void Iniciar() {
        ServerSocket Servidor = null;
        BufferedReader Lector = null;
        String Direccion = "";
        DataOutputStream dos = null;
        try {
            Servidor = new ServerSocket(puerto);
        } catch (IOException ex) {
            System.out.println("Error al crear socket de servidor: " + ex.toString());
        }
        try {
            Socket = Servidor.accept();
        } catch (IOException ex) {
            System.out.println("Error al crear socket de conexión: " + ex.toString());
        }
        try {
            dos = new DataOutputStream(Socket.getOutputStream());
        } catch (IOException ex) {
            System.out.println("Error al crear salida de datos: " + ex.toString());
        }
        try {
            Lector = new BufferedReader(new InputStreamReader(Socket.getInputStream()));
        } catch (IOException ex) {
            System.out.println("Error al crear lector de datos");
        }
        System.out.println("Se inició el servidor, esperando peticiones");
        while (Lector!=null) {
            try {
                Direccion = Lector.readLine();
                System.out.println("Se pidió el archivo: " + Direccion);
            } catch (IOException ex) {
                System.out.println("Error al leer datos: " + ex.toString());
            }
            File Archivo = new File(Direccion);
            if (Archivo.exists()) {
                EnviarArchivo(Archivo,dos);
            } else {
                System.out.println("El archivo no existe");
                try {
                    dos.writeBoolean(false);
                } catch (IOException ex) {
                    System.out.println("Error al enviar respuesta: " + ex.toString());
                }
            }
            Lector=null;
        }
    }
    public void EnviarArchivo(File Archivo, DataOutputStream dos) {
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            bis = new BufferedInputStream(new FileInputStream(Archivo));
        } catch (FileNotFoundException ex) {
            System.out.println("Archivo no encontrado: " + ex.toString());
        }
        try {
            bos = new BufferedOutputStream(Socket.getOutputStream());
        } catch (IOException ex) {
            System.out.println("Error al crear buffer de salida: " + ex.toString());
        }
        try {
            dos.writeBoolean(true);
        } catch (IOException ex) {
            System.out.println("Error al enviar respuesta: " + ex.toString());
        }
        try {
            dos.writeUTF(Archivo.getName());
        } catch (IOException ex) {
            System.out.println("No se encontró nombre de archivo: " + ex.toString());
        }
        byte[] byteArray = new byte[8192];
        int in;
        try {
            System.out.println("Enviando archivo");
            while ((in = bis.read(byteArray)) != -1) {
                bos.write(byteArray, 0, in);
            }
        } catch (IOException ex) {
            System.out.println("Error al leer bits: " + ex.toString());
        }
        try {
            bis.close();
            bos.close();
        } catch (IOException ex) {
            System.out.println("Error al cerrar buffers de datos: " + ex.toString());
        }
    }
}
