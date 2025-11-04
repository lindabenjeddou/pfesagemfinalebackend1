-- Migration pour corriger le problème dtype dans demande_intervention
-- Exécuter ce script sur votre base de données MySQL

-- Étape 1: Vérifier la structure actuelle
DESCRIBE demande_intervention;

-- Étape 2: Renommer la colonne type_demande en dtype
ALTER TABLE demande_intervention CHANGE COLUMN type_demande dtype VARCHAR(255);

-- Étape 3: Vérifier que la migration a fonctionné
DESCRIBE demande_intervention;

-- Étape 4: Optionnel - Vérifier les données existantes
SELECT id, dtype, description, statut FROM demande_intervention LIMIT 5;
