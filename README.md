# 🪟 Aluglass — Sistema de Gestión Comercial y Control de Cuentas Corrientes

> **Sistema de Gestión Administrativo-Contable** desarrollado para la optimización de procesos de venta, control de cuentas corrientes, facturación y gestión presupuestaria en el sector de la carpintería de aluminio y vidrio.

---

## 📌 Descripción del Proyecto

**Aluglass** es una solución de software orientada a resolver las necesidades operativas y administrativas de empresas dedicadas a la fabricación y comercialización de aberturas y cerramientos. 

El sistema centraliza la gestión de clientes, el seguimiento de presupuestos y el estado financiero de cuentas corrientes, permitiendo un control preciso de saldos, registros de pagos y generación de documentación comercial.

---

## 🚀 Características Principales

* 👥 **Gestión de Clientes:** Alta, baja, modificación y consulta detallada de datos de clientes.
* 💳 **Control de Cuentas Corrientes:** Registro de movimientos financieros, acreditación de pagos, saldos pendientes y trazabilidad de deudas.
* 📋 **Presupuestación y Facturación:** Creación, cálculo de costos y emisión de presupuestos personalizados.
* 📊 **Consultas y Reportes:** Filtrado rápido de transacciones, estados de cuenta e historial comercial mediante consultas SQL optimizadas.

---

## 🛠️ Stack Técnico & Arquitectura

* **Lenguaje Principal:** Java (Programación Orientada a Objetos - POO)
* **Interfaz Gráfica (UI):** JavaFX
* **Base de Datos:** SQL / PostgreSQL / MySQL
* **Conectividad:** JDBC (Java Database Connectivity)
* **Control de Versiones:** Git & GitHub

---

## 📐 Análisis y Diseño del Sistema

El desarrollo fue precedido por una etapa rigurosa de análisis de sistemas para garantizar la integridad de los datos y la escalabilidad del software:

### 1. Diagramas de Flujo de Datos (DFD)
* **Nivel 0 (Diagrama de Contexto):** Delimitación de fronteras del sistema, entidades externas (Clientes, Proveedores, Administración) y flujos de información principales.
* **Nivel 1:** Descomposición funcional de procesos clave (Gestión de Cuentas Corrientes, Emisión de Presupuestos, Registro de Cobros).

### 2. Modelo Entidad-Relación (DER)
* Diseño conceptual, lógico y físico de la base de datos relacional.
* Aplicación de reglas de normalización (1FN, 2FN, 3FN) e integridad referencial (Claves Primarias y Foráneas) para evitar redundancias.

> 💡 *Podés consultar la documentación técnica y los diagramas en la carpeta [`/docs`](./docs).*

---

## 📂 Estructura del Repositorio

```text
aluglass/
├── src/                    # Código fuente en Java
│   ├── controller/         # Controladores de interfaz y eventos
│   ├── model/              # Modelos de datos y lógica de negocio
│   ├── repository/         # Capa de acceso a datos (DAO / JDBC)
│   └── view/               # Vistas e interfaces de usuario en JavaFX (.fxml)
├── docs/                   # Documentación técnica (DER, DFD, UML, Requerimientos)
├── database/               # Scripts SQL (Creación de tablas, Triggers, Stored Procedures)
├── README.md               # Documentación general del proyecto
└── .gitignore
