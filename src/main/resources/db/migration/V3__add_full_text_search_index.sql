-- Migración para añadir índice de búsqueda de texto completo en tickets
-- Este índice GIN mejorará significativamente el rendimiento de las búsquedas de texto

-- Crear índice GIN para búsqueda de texto completo en título y descripción
CREATE INDEX tickets_text_search_idx ON tickets 
USING GIN (to_tsvector('english', COALESCE(title, '') || ' ' || COALESCE(description, '')));

-- Comentario explicativo sobre el índice
COMMENT ON INDEX tickets_text_search_idx IS 'Índice GIN para búsqueda de texto completo en campos title y description de tickets';
