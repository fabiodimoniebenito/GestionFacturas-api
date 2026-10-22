-- Catálogo básico de categorías de gasto. Idempotente: no falla ni duplica si ya existen filas.
-- deducible_defecto = false en todas: la app no sugiere nada fiscal.
INSERT INTO categoria (nombre, deducible_defecto) VALUES
    ('Suministros', false),
    ('Software y suscripciones', false),
    ('Material de oficina', false),
    ('Equipos informáticos', false),
    ('Transporte', false),
    ('Formación', false),
    ('Servicios profesionales', false),
    ('Marketing y publicidad', false),
    ('Alquiler', false),
    ('Comidas y dietas', false),
    ('Otros', false)
ON CONFLICT (nombre) DO NOTHING;
