package ec.edu.taller;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;


public class GestorImpresiones {
    private Queue<TrabajoImpresion> colaTrabajos;
    private int capacidadMaxima;

    public GestorImpresiones(int capacidadMaxima) {
        this.capacidadMaxima = capacidadMaxima;

        this.colaTrabajos = new PriorityQueue<>(
                Comparator.comparingInt(TrabajoImpresion::getPrioridad));
    }

    public boolean registrarTrabajo(TrabajoImpresion trabajo) {
        if (trabajo == null) {
            return false;
        }
        if (codigoInvalido(trabajo.getCodigo())) {
            return false;
        }
        if (existeTrabajo(trabajo.getCodigo())) {
            return false;
        }
        if (colaTrabajos.size() >= capacidadMaxima) {
            return false;
        }
        return colaTrabajos.offer(trabajo);
    }

    public boolean existeTrabajo(String codigo) {
        if (codigoInvalido(codigo)) {
            return false;
        }
        for (TrabajoImpresion t : colaTrabajos) {
            if (codigo.equals(t.getCodigo())) {
                return true;
            }
        }
        return false;
    }

    public TrabajoImpresion consultarSiguienteTrabajo() {
        return colaTrabajos.peek();
    }

    public TrabajoImpresion procesarSiguienteTrabajo() {
        TrabajoImpresion trabajo = colaTrabajos.poll();
        if (trabajo != null) {
            trabajo.setEstado("PROCESADO");
        }
        return trabajo;
    }

    public int contarTrabajosPendientes() {
        return colaTrabajos.size();
    }

    public int consultarEspaciosDisponibles() {
        return capacidadMaxima - colaTrabajos.size();
    }

    public List<TrabajoImpresion> listarTrabajos() {

        List<TrabajoImpresion> lista = new ArrayList<>(colaTrabajos);
        lista.sort(Comparator.comparingInt(TrabajoImpresion::getPrioridad));
        return lista;
    }

    private boolean codigoInvalido(String codigo) {
        return codigo == null || codigo.trim().isEmpty();
    }
}
