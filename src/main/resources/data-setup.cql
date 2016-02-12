-- Ustawienia programu

-- Etap wyboru zmiennych

-- liczba zmiennych które musi wybrać użytkownik, docelowo 7
INSERT INTO experiment.text_resources (key, value) VALUES ('Setup.Variables.VariablesCount', '7');


-- Etap iteracji

-- predefiniowana długość sekwencji
INSERT INTO experiment.text_resources (key, value) VALUES ('Setup.Trial.SequenceLength', '3');
-- koszt odsłonięcia jednej wartości
INSERT INTO experiment.text_resources (key, value) VALUES ('Setup.Trial.UnitPrice', '10');
-- całkowita liczba punktów do zużycia
INSERT INTO experiment.text_resources (key, value) VALUES ('Setup.Trial.TotalScore', '70');
-- minimalna liczba zmiennych, które można wybrać i których wybranie nic nie kosztuje
INSERT INTO experiment.text_resources (key, value) VALUES ('Setup.Trial.MinSelectedVariables', '3');
-- maksymalna liczba zmiennych które można wybrać
INSERT INTO experiment.text_resources (key, value) VALUES ('Setup.Trial.MaxSelectedVariables', '10');
-- konfiguracja sekwencji, docelowo CCCAAAAA|CA
INSERT INTO experiment.text_resources (key, value) VALUES ('Setup.Trial.Sequences', 'AAAAACCC|AC');
-- liczba najważniejszych zmiennych, które ma wybrać użytkownik
INSERT INTO experiment.text_resources (key, value) VALUES ('Setup.Trial.EssentialVariables', '2');

-- Etap loterii

-- liczba ostatnich iteracji branych pod uwagę
INSERT INTO experiment.text_resources (key, value) VALUES ('Setup.Lottery.LastIterationsCount', '2');
