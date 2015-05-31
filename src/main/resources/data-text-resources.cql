INSERT INTO experiment.text_resources (key, value) VALUES ('Titles.Thanks', 'Podziękowania');
INSERT INTO experiment.text_resources (key, value) VALUES ('Titles.Questionnaire', 'Kwestionariusz');
INSERT INTO experiment.text_resources (key, value) VALUES ('Titles.Instruction', 'Instrukcja');
INSERT INTO experiment.text_resources (key, value) VALUES ('Titles.Trial', 'Badanie');
INSERT INTO experiment.text_resources (key, value) VALUES ('Titles.Lottery', 'Loteria');
INSERT INTO experiment.text_resources (key, value) VALUES ('Titles.MostImportantVariables', 'Wybór najważniejszych czynników');
INSERT INTO experiment.text_resources (key, value) VALUES ('Titles.ExplanationQuestion', 'Dlaczego wybrane zostały takie czynniki');
INSERT INTO experiment.text_resources (key, value) VALUES ('Titles.ConfidenceQuestion', 'Jak bardzo jesteś pewien/pewna swojego wyboru');
INSERT INTO experiment.text_resources (key, value) VALUES ('Titles.PersonalData', 'Metryczka');
INSERT INTO experiment.text_resources (key, value) VALUES ('Titles.Main', 'Badanie');
INSERT INTO experiment.text_resources (key, value) VALUES ('Titles.Login', 'Logowanie');
INSERT INTO experiment.text_resources (key, value) VALUES ('Titles.VariablesSelection', 'Wybór czynników');
INSERT INTO experiment.text_resources (key, value) VALUES ('Titles.VariablesOrdering', 'Szeregowanie czynników');
INSERT INTO experiment.text_resources (key, value) VALUES ('Titles.VariablesScoring', 'Określanie ważności czynników');
INSERT INTO experiment.text_resources (key, value) VALUES ('Titles.CannotContinue', 'Nie można kontynuować');
INSERT INTO experiment.text_resources (key, value) VALUES ('Titles.EmailAddress', 'Adres email');
INSERT INTO experiment.text_resources (key, value) VALUES ('Titles.Justifications', 'Uzasadnienia');
INSERT INTO experiment.text_resources (key, value) VALUES ('Titles.MissingVariables', 'Brakujące zmienne');

INSERT INTO experiment.text_resources (key, value) VALUES ('Labels.Lottery', 'Wybieram loterię');
INSERT INTO experiment.text_resources (key, value) VALUES ('Labels.NotLottery', 'Wybieram zakład');
INSERT INTO experiment.text_resources (key, value) VALUES ('Labels.ConfirmConfidence', 'OK');
INSERT INTO experiment.text_resources (key, value) VALUES ('Labels.Confidence', 'Pewność');
INSERT INTO experiment.text_resources (key, value) VALUES ('Labels.Score', 'Punkty');
INSERT INTO experiment.text_resources (key, value) VALUES ('Labels.Minus', 'Bessa');
INSERT INTO experiment.text_resources (key, value) VALUES ('Labels.Plus', 'Hossa');
INSERT INTO experiment.text_resources (key, value) VALUES ('Labels.Rank', 'Ważność');
INSERT INTO experiment.text_resources (key, value) VALUES ('Labels.Select', 'Wybierz');
INSERT INTO experiment.text_resources (key, value) VALUES ('Labels.NextButton', 'Dalej');
INSERT INTO experiment.text_resources (key, value) VALUES ('Labels.LoginButton', 'Zaloguj');
INSERT INTO experiment.text_resources (key, value) VALUES ('Labels.LoginCodeField', 'Kod logowania');
INSERT INTO experiment.text_resources (key, value) VALUES ('Labels.ExplanationQuestion', 'Podaj wyjaśnienie dlaczego wybrałeś właśnie takie wskaźniki do podjęcia decyzji?');
INSERT INTO experiment.text_resources (key, value) VALUES ('Labels.ConfidenceQuestion', 'Jak bardzo jesteś pewien swojego wyboru?');
INSERT INTO experiment.text_resources (key, value) VALUES ('Labels.TrialQuestion', 'Czy na rynku papierów wartościowych jest hossa czy bessa?');
INSERT INTO experiment.text_resources (key, value) VALUES ('Labels.EmailQuestion', 'Wprowadź swój adres email');
INSERT INTO experiment.text_resources (key, value) VALUES ('Labels.MissingVariables', 'Prosimy o podanie informacji, które Ty uznałbyś za diagnostyczne, gdybyś miał odpowiedzieć na pytanie "Czy na rynku papierów wartościowych jest hossa czy bessa?", niezależnie od tego, czy informacje te pojawiły się w badaniu czy nie.');

INSERT INTO experiment.text_resources (key, value) VALUES ('Notifications.LoseTheLottery', 'Została wylosowana kula czarna. Przegrana');
INSERT INTO experiment.text_resources (key, value) VALUES ('Notifications.WonTheLottery', 'Została wylosowana kula biała. Wygrana');
INSERT INTO experiment.text_resources (key, value) VALUES ('Notifications.ConfidenceValueInvalid', 'Wartość musi być z przedziału od 50 do 100');
INSERT INTO experiment.text_resources (key, value) VALUES ('Notifications.RankValueOutOfRange', 'Wartość musi być z przedziału od 1 do 100');
INSERT INTO experiment.text_resources (key, value) VALUES ('Notifications.MustSelectAtLeastNVariables', 'Należy wybrać przynajmniej %d czynników');
INSERT INTO experiment.text_resources (key, value) VALUES ('Notifications.MustSelectAtMostNVariables', 'Należy wybrać nie więcej niż %d czynników');
INSERT INTO experiment.text_resources (key, value) VALUES ('Notifications.TotalScoreMustBe100', 'Suma wszystkich punktów powinna być równa 100');
INSERT INTO experiment.text_resources (key, value) VALUES ('Notifications.LoginFailed', 'Brak dostępu');
INSERT INTO experiment.text_resources (key, value) VALUES ('Notifications.LoginSuccessful', 'Zalogowano');
INSERT INTO experiment.text_resources (key, value) VALUES ('Notifications.MustSelectExactlyNEssentialVariables', 'Należy wybrać dokładnie %s zmienne');
INSERT INTO experiment.text_resources (key, value) VALUES ('Notifications.MustSelectExactlyNVariables', 'Należy wybrać dokładnie %s zmiennych');
INSERT INTO experiment.text_resources (key, value) VALUES ('Notifications.MustChooseLotteryOrBet', 'Należy wybrać loterię lub zakład');

-- powiadomienie w czasie iteracji, jak ktors chce iść dalej nie wybierając uprzednio odpowiedzi
INSERT INTO experiment.text_resources (key, value) VALUES ('Notifications.NeedToSelectAnswer', 'Należy wybrać odpowiedź');

INSERT INTO experiment.text_resources (key, value) VALUES ('Notifications.CorrectAnswerProvided', 'Wygrałeś/aś zakład - Twoja odpowiedź w pytaniu %d była poprawna');
INSERT INTO experiment.text_resources (key, value) VALUES ('Notifications.NoCorrectAnswerProvided', 'Przegrałeś/aś zakład - Twoja odpowiedź w pytaniu %d była błędna');
INSERT INTO experiment.text_resources (key, value) VALUES ('Notifications.ValidationError', 'Uzupełnij poprawnie wszystkie pola');
INSERT INTO experiment.text_resources (key, value) VALUES ('Notifications.InvalidEmailAddress', 'Podany adres email jest nieprawidłowy');
INSERT INTO experiment.text_resources (key, value) VALUES ('Notifications.InvalidMissingVariables', 'Nieprawidło wprowadzona informacja o brakujących zmiennych');

INSERT INTO experiment.text_resources (key, value) VALUES ('Instructions.TrialInstruction', '
W kolejnym etapie Twoim zadaniem będzie udzielenie odpowiedzi na pytanie: <quote>Czy na rynku papierów
wartościowych jest hossa czy bessa?</quote> w oparciu o podane informacje. Poprosimy Cię o udzielenie odpowiedzi na to pytanie dziesięć razy.
<p/>
Zgodnie z zapowiedzią, pięciu uczestników otrzyma nagrody pieniężne wysokości 500 zł. Nagrody otrzymają ci, którzy uzyskają najwyższą trafność odpowiedzi oraz zachowają największą liczbę z przypisanych każdej osobie 70 punktów.
Punkty można zachować lub wydać, zależy to od tego ile informacji wykorzystasz, żeby odpowiedzieć na pytanie: "Czy na rynku papierów wartościowych jest hossa czy bessa?"
<p/>
Za chwilę pojawi się 16 informacji z rynku kapitałowego. Pierwsze 3 nic nie kosztuje, ale każda następna kosztuje 10 punktów, które są odliczane od 70 punktów.
<p/>
Informacji jest 16. Musisz wykorzystać 3 (darmowe) i możesz wykorzystać co najwyżej 10 spośród nich (każda powyżej trzech kosztuje 10 punktów).
<p/>
Licznik wskazujący aktualną liczbę punktów znajduje się w górnej części ekranu.
<p/>
Jeśli chcesz wziąć udział w konkursie, musisz na końcu podać swój e-mail.
<p/>
');
INSERT INTO experiment.text_resources (key, value) VALUES ('Instructions.QuestionnaireInstruction', 'Instrukcja do kwestionariusza');
INSERT INTO experiment.text_resources (key, value) VALUES ('Instructions.InitialInstruction', '
Zapraszamy do udziału w badaniu finansowanym z grantu nr 2014/13/B/HS4/01533, przyznanego przez Narodowe Centrum Nauki. Badanie jest
w pełni anonimowe i służy wyłącznie do celów naukowych. Udział w badaniu jest dobrowolny i w każdej chwili możesz się z niego wycofać. 
<p/>
Celem badania jest ustalenie jakimi wskaźnikami posługują się prywatni inwestorzy przy ocenie trendów rynkowych. Badanie zajmuje około 30 minut. Niektórzy badani otrzymają nagrody finansowe – szczegóły w dalszej części badania.
<p/>
W przypadku jakichkolwiek pytań i wątpliwości prosimy o kontakt:
<p/>
Agnieszka Lewandowska <a href="mailto:amyrczek-lewandowska@swps.edu.pl">amyrczek-lewandowska@swps.edu.pl</a>
<p/>
Sandra Siemieniec <a href="mailto:ssiemieniec@st.swps.edu.pl">ssiemieniec@st.swps.edu.pl</a>
<p/>
Jeśli wyrażasz zgodę na udział w badaniu, kliknij przycisk "Dalej".
');

INSERT INTO experiment.text_resources (key, value) VALUES ('Instructions.Lottery', '
Proponujemy Ci wybór między zakładem, że trafnie odpowiedziałeś, że jest hossa lub bessa a loterią. 
<p/>
Jeżeli wybierzesz zakład, wygrasz 50 złotych jeśli trafnie określiłeś trend rynkowy w jednym z dwóch ostatnich pytań - 9 lub 10.
Nie wiesz jednak które to będzie pytanie. Przez komputer wylosowane będzie tylko jedno z nich, czyli są jednakowe szanse na to, że będzie to pytanie 9 lub 10.
<p/>
Jeżeli wybierzesz loterię możesz wygrać 50 złotych, jeśli z urny będzie wylosowana przez komputer kula biała. W urnie jest 100 kul, a białych jest %s.
');
INSERT INTO experiment.text_resources (key, value) VALUES ('Instructions.LotteryQuestion', 'Kliknij w jedno z dwóch okienek:');

INSERT INTO experiment.text_resources (key, value) VALUES ('Instructions.Thanks',
'
<h1>Dziękujemy za udział w badaniu</h1>
<p>W celu uzyskania wyników końcowych skontaktuj się z nami pod <a href="mailto:amyrczek-lewandowska@swps.edu.pl">tym adresem</a>.</p>
');

INSERT INTO experiment.text_resources (key, value) VALUES ('Instructions.VariablesSelection',
'
Na ekranie znajduje się 16 wskaźników z rynku kapitałowego. Wybierz 7 z nich, najważniejszych i wystarczających Twoim zdaniem
do udzielenia odpowiedzi na pytanie: <strong>Czy na rynku papierów wartościowych jest hossa czy bessa?</strong>
');

INSERT INTO experiment.text_resources (key, value) VALUES ('Instructions.VariablesOrdering',
'
Poniżej znajduje się 7 wskaźników, które uznałeś za ważne w określeniu trendu rynkowego.
Prosimy o uszeregowanie ich w kolejności od najważniejszego (na górze strony) do najmniej ważnego
(na dole strony). Przeciągnij wskaźnik myszą w wybrane miejsce.
');

INSERT INTO experiment.text_resources (key, value) VALUES ('Instructions.VariablesScoring',
'
Poniżej znajduje się lista wybranych przez Ciebie wskaźników. Prosimy przypisz im wartości, rozdzielając
100 punktów między wszystkie z nich w ten sposób, aby liczba przypisanych punktów odzwierciedlała ważność wskaźnika.
');

INSERT INTO experiment.text_resources (key, value) VALUES ('Instructions.Trial', '
Pytanie %s. Wybierz co najmniej 3 i co najwyżej 10 wskaźników. Informacja odsłoni się gdy klikniesz na wybranym polu.
<p/>
Przeczytaj informacje i odpowiedz na pytanie: Czy na rynku papierów wartościowych jest hossa czy bessa?
');

INSERT INTO experiment.text_resources (key, value) VALUES ('Instructions.ConfidenceQuestion',
'
Na ile jesteś przekonany, że odpowiedziałeś prawidłowo na pytanie o trendy rynkowe?
<p/>
Zaznacz swoje przekonanie na skali 50%-100%. Ponieważ odpowiedzi są tylko dwie, 50% oznacza, że zgadywałeś, a 100% - że jesteś całkowicie pewien swojej odpowiedzi.
<p/>
Jak bardzo jesteś pewien, że odpowiedziałeś prawidłowo? Przeciągnij suwak myszą w wybrane miejsce.

');

INSERT INTO experiment.text_resources (key, value) VALUES ('Instructions.ExplanationQuestion',
'
W kilku słowach uzasadnij dlaczego uznałeś te wskaźniki za diagnostyczne. Wpisz komentarz.
');

INSERT INTO experiment.text_resources (key, value) VALUES ('Instructions.MostImportantVariables',
'
Wybierz te dwa wskaźniki, które miały największy wpływ na Twoją decyzję.
');

INSERT INTO experiment.text_resources (key, value) VALUES ('Instructions.Questionnaire',
'
Instrukcja do kwestionariusza
');

INSERT INTO experiment.text_resources (key, value) VALUES ('Instructions.PersonalData', '');

INSERT INTO experiment.text_resources (key, value) VALUES ('Instructions.EmailAddress', '
Jeżeli chcesz wziąć udział w konkursie, wpisz swój email.
');

INSERT INTO experiment.text_resources (key, value) VALUES ('Instructions.Justifications', '
Poniżej znajduje się lista wskaźników, które wybrałeś w badaniu aby odpowiedzieć na pytanie o trendy
rynkowe. Dlaczego uznałeś je za diagnostyczne? Wpisz komentarz.
');
