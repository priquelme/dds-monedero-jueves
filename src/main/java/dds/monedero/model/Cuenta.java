package dds.monedero.model;

import dds.monedero.exceptions.MaximaCantidadDepositosException;
import dds.monedero.exceptions.MaximoExtraccionDiarioException;
import dds.monedero.exceptions.MontoNegativoException;
import dds.monedero.exceptions.SaldoMenorException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Cuenta {

  private double saldo = 0;
  private List<Movimiento> movimientos = new ArrayList<>();

  public Cuenta() {
    saldo = 0;
  }

  public Cuenta(double montoInicial) {
    saldo = montoInicial;
  }

  public void setMovimientos(List<Movimiento> movimientos) {
    this.movimientos = movimientos;
  }

  public void poner(double cuanto) { //Duplicated Code - Long Method
    this.montoNegativo(cuanto);
    this.maximaCantidadDepositos();
    new Movimiento(LocalDate.now(), cuanto, true).agregateA(this);
  }

  public void sacar(double cuanto) { //Duplicated Code - Long Method
    this.montoNegativo(cuanto);
    this.saldoMenor(cuanto);
    this.limiteDeExtraccion(cuanto);
    new Movimiento(LocalDate.now(), cuanto, false).agregateA(this);
  }

  public void montoNegativo(double cuanto) {
    if (cuanto <= 0)
      throw new MontoNegativoException(cuanto + ": el monto a ingresar debe ser un valor positivo");
  }

  public void maximaCantidadDepositos() {
    if (getMovimientos().stream().filter(Movimiento::getDeposito).count() >= 3)
      throw new MaximaCantidadDepositosException("Ya excedio los " + 3 + " depositos diarios");
  }

  public void saldoMenor(double cuanto) {
    if (getSaldo() - cuanto < 0)
      throw new SaldoMenorException("No puede sacar mas de " + getSaldo() + " $");
  }

  public void limiteDeExtraccion(double cuanto) {
    double montoExtraidoHoy = getMontoExtraidoA(LocalDate.now());
    double limite = 1000 - montoExtraidoHoy;
    if (cuanto > limite)
      throw new MaximoExtraccionDiarioException("No puede extraer mas de $ " + 1000
          + " diarios, límite: " + limite);
  }

  public void agregarMovimiento(Movimiento movimiento) { // Long Parameter List
    movimientos.add(movimiento);
  }

  public double getMontoExtraidoA(LocalDate fecha) {
    return getMovimientos().stream()
        .filter(movimiento -> movimiento.fueExtraido(fecha))
        .mapToDouble(Movimiento::getMonto)
        .sum();
  }

  public List<Movimiento> getMovimientos() {
    return movimientos;
  }

  public double getSaldo() {
    return saldo;
  }

  public void setSaldo(double saldo) {
    this.saldo = saldo;
  }
}
