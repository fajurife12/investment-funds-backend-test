# SQL — Consultas BTG

Base de datos SQLite con el modelo de datos y las consultas solicitadas.

## Archivos

- `btg.sql` — script completo: schema, datos de prueba y consultas
- `btg.db`  — base de datos SQLite lista para abrir

## Cómo ejecutar

### Opción 1 — DB Browser for SQLite
Abre `btg.db` directamente con [DB Browser for SQLite](https://sqlitebrowser.org).

### Opción 2 — SQLite CLI
```bash
sqlite3 btg.db
sqlite> .read btg.sql
```

### Opción 3 — VS Code
Instala la extensión **SQLite Viewer** y abre el archivo `btg.db`.

## Consulta implementada

Obtener los nombres de los clientes que tienen inscrito algún producto
disponible solo en las sucursales que visitan.

**Resultado esperado:** Carlos, Lucía