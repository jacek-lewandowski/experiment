INSERT INTO experiment.surveys (category, id, caption, required, validator_name, validator_params) VALUES (
    'personalData', 1, 'Płeć', true,
    'selection', {'1':'kobieta', '2':'mężczyzna'}
);
INSERT INTO experiment.surveys (category, id, caption, required, validator_name, validator_params) VALUES (
    'personalData', 2, 'Wiek', true,
    'range', {'min':'15', 'max':'80', 'message':'Wartość musi być z przedzialu od 15 do 80'}
);
INSERT INTO experiment.surveys (category, id, caption, required, validator_name, validator_params) VALUES (
    'personalData', 3, 'Staż inwestycyjny', true,
    'range', {'min':'0', 'max':'80', 'message':'Wartość musi być z przedzialu od 0 do 80'}
);
INSERT INTO experiment.surveys (category, id, caption, required, validator_name, validator_params) VALUES (
    'personalData', 4, 'Wykształcenie', true,
    'selection', {'1':'ekonomiczne', '2':'humanistyczne', '3':'biologiczne', '4':'techniczne'}
);
INSERT INTO experiment.surveys (category, id, caption, required, validator_name, validator_params) VALUES (
    'personalData', 5, 'Czas poświecany na inwestowanie', true,
    'selection', {'1':'raz na miesiąc lub rzadziej', '2':'kilka razy w miesiącu', '3':'kilka razy w tygodniu', '4':'codziennie'}
);
INSERT INTO experiment.surveys (category, id, caption, required, validator_name, validator_params) VALUES (
    'personalData', 6, 'Adres email', true,
    'length', {'min':'3', 'max':'100', 'message':'Nieprawidłowy adres email'}
);

