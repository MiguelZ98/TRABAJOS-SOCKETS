package clientearchivos;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class Cliente {

    private String ip;
    private int puerto;
    private String ruta;
    private Socket Socket;

    public Cliente(String[] args) {
        ip = args[0];
        puerto = Integer.valueOf(args[1]);
        ruta = args[2];
        Socket = null;
    }

    public void Iniciar() {
        Boolean Recibir = false;
        DataInputStream dis = null;
        PrintWriter Escritor=null;
        try {
            Socket = new Socket(ip, puerto);
        } catch (IOException ex) {
            System.out.println("No se ha podido conectar con el host");
        }
        try {
            dis = new DataInputStream(Socket.getInputStream());
        } catch (IOException ex) {
            System.out.println("Error al crear entrada de datos");
        }
        try {
            Escritor = new PrintWriter(Socket.getOutputStream(), true);
        } catch (IOException ex) {
            System.out.println("Error al crear escritor de datos");
        }
        try {
            Escritor.println(ruta);
        } catch (Exception e) {
            System.out.println("Error al enviar petición al servidor");
        }
        try {
            Recibir = dis.readBoolean();
        } catch (IOException ex) {
            System.out.println("Error al leer respuesta del servidor");
        }
        if (Recibir) {
            RecibirArchivo(dis);
        }else{
            System.out.println("El archivo no se encuentra/no existe");
        }
    }

    public void RecibirArchivo(DataInputStream dis) {
        System.out.println("Recibiendo archivo");
        try {
            byte[] DatosRecibidos = new byte[1024];
            int EntradaDeBits;
            BufferedInputStream bis = null;
            BufferedOutputStream bos = null;
            String NombreDeArchivo = "";
            try {
                bis = new BufferedInputStream(Socket.getInputStream());
            } catch (IOException ex) {
                System.out.println("Error al crear buffer de entrada de datos");
            }
            try {
                NombreDeArchivo = dis.readUTF();
            } catch (IOException ex) {
                System.out.println("Error al leer nombre de archivo recibido");
            }
            File Directorio = new File("");
            String Ruta = Directorio.getAbsolutePath()+"\\Downloads\\"+NombreDeArchivo;
            System.out.println("El archivo se guardará en: "+Ruta);
            try {
                bos = new BufferedOutputStream(new FileOutputStream(Ruta));
            } catch (FileNotFoundException ex) {
                System.out.println("Archivo perdido");
            }
            while ((EntradaDeBits = bis.read(DatosRecibidos)) != -1) {
                bos.write(DatosRecibidos, 0, EntradaDeBits);
            }
            bos.close();
        } catch (IOException ex) {
            System.out.println("Error en la lectura y/o escritura de bits");
        }
        try {
            dis.close();
        } catch (IOException ex) {
            System.out.println("Error al cerrar conexiones");
        }
    }
}
