package com.fabio.GestionFacturas.domain.ingreso;

/** Estado de cobro de un Ingreso: indica si la factura emitida al cliente ya se ha cobrado. */
public enum EstadoCobro {

    PENDIENTE, //factura emitida, aún no cobrada
    COBRADA    //el usuario registró el cobro
}
