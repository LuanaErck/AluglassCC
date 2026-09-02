package utilidades;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Respaldo 
{
    // Días a retener del historial de bases de datos
    private static final int DIAS_A_RETENER = 10; 

    /**
     * Respalda la Base de Datos y la carpeta de Comprobantes en la unidad indicada.
     * 
     * @param letraPendrive Letra de la unidad USB (ejemplo: "E" o "F")
     * @return true si el respaldo de la base de datos fue exitoso, false en caso contrario.
     */
    public static boolean respaldarTodoEnPendrive(String letraPendrive) 
    {
        boolean exitoBase = respaldarBaseDatos(letraPendrive);
        respaldarCarpetaComprobantes(letraPendrive);
        return exitoBase;
    }

    /**
     * Respalda la base de datos con rotación diaria.
     */
    private static boolean respaldarBaseDatos(String letraPendrive) 
    {
        try 
        {
            // 1. Verificar existencia de la base local
            File origen = new File("clientes_aluglass.db"); 
            if (!origen.exists()) 
            {
                System.err.println("[Respaldo] Error: No se encontró la base de datos local.");
                return false;
            }

            // 2. Crear carpeta contenedora en el pendrive
            File carpetaPendrive = new File(letraPendrive + ":/Respaldos_Aluglass");
            if (!carpetaPendrive.exists()) 
            {
                if (!carpetaPendrive.mkdirs()) 
                {
                    System.err.println("[Respaldo] Error: El pendrive (" + letraPendrive + ":) no está disponible.");
                    return false;
                }
            }

            // 3. Generar nombre basado únicamente en la FECHA (1 respaldo único por día)
            String fechaHoy = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String nombreBackup = "aluglass_backup_" + fechaHoy + ".db";
            File destino = new File(carpetaPendrive, nombreBackup);

            // Copia y reemplaza la del mismo día si ya existía
            Files.copy(origen.toPath(), destino.toPath(), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("[Respaldo] Base de datos guardada en: " + destino.getAbsolutePath());

            // 4. Limpieza de respaldos antiguos (más de 10 días)
            limpiarRespaldosAntiguos(carpetaPendrive);

            return true;

        } 
        catch (Exception e) 
        {
            System.err.println("[Respaldo] Excepción al respaldar la base de datos:");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Copia / Sincroniza la carpeta local de comprobantes (PDFs) hacia el pendrive.
     */
    private static void respaldarCarpetaComprobantes(String letraPendrive) 
    {
        try 
        {
            File origenPDFs = new File("recibos"); 
            boolean carpetaExiste = origenPDFs.exists();

            if (carpetaExiste) 
            {
                Path origenPath = origenPDFs.toPath();
                Path destinoPath = Paths.get(letraPendrive + ":/Respaldos_Aluglass/recibos");

                if (!Files.exists(destinoPath)) 
                {
                    Files.createDirectories(destinoPath);
                }

                // Copiar recursivamente carpetas y archivos PDF
                Files.walkFileTree(origenPath, new SimpleFileVisitor<Path>() 
                {
                    @Override
                    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException 
                    {
                        Path targetDir = destinoPath.resolve(origenPath.relativize(dir));
                        if (!Files.exists(targetDir))
                        {
                            Files.createDirectory(targetDir);
                        }
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException 
                    {
                        Files.copy(file, destinoPath.resolve(origenPath.relativize(file)), StandardCopyOption.REPLACE_EXISTING);
                        return FileVisitResult.CONTINUE;
                    }
                });

                System.out.println("[Respaldo] Carpeta de comprobantes sincronizada en el pendrive.");
            } 
            else 
            {
                System.out.println("[Respaldo] La carpeta local de comprobantes aún no existe. No se requirió copia.");
            }

        } 
        catch (Exception e) 
        {
            System.err.println("[Respaldo] Excepción al copiar la carpeta de comprobantes:");
            e.printStackTrace();
        }
    }

    /**
     * Revisa los archivos del pendrive y borra los .db que superen el límite de días configurado.
     */
    private static void limpiarRespaldosAntiguos(File carpeta) 
    {
        File[] archivos = carpeta.listFiles((dir, name) -> name.startsWith("aluglass_backup_") && name.endsWith(".db"));
        if (archivos == null) return;

        long limiteTiempo = System.currentTimeMillis() - ((long) DIAS_A_RETENER * 24 * 60 * 60 * 1000);

        for (File archivo : archivos) 
        {
            if (archivo.lastModified() < limiteTiempo) 
            {
                boolean borrado = archivo.delete();
                
                if (borrado)
                {
                    System.out.println("[Respaldo] Se eliminó el respaldo antiguo: " + archivo.getName());
                }
            }
        }
    }
}