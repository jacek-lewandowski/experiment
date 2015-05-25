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
INSERT INTO experiment.text_resources (key, value) VALUES ('Labels.ExplanationQuestion', 'Podaj wyjaśnienie dlaczego wybrałeś/wybrałaś właśnie takie wskaźniki do podjęcia decyzji?');
INSERT INTO experiment.text_resources (key, value) VALUES ('Labels.ConfidenceQuestion', 'Jak bardzo jesteś pewien/pewna swojego wyboru?');

INSERT INTO experiment.text_resources (key, value) VALUES ('Notifications.LoseTheLottery', 'Przegrana');
INSERT INTO experiment.text_resources (key, value) VALUES ('Notifications.WonTheLottery', 'Wygrana');
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

INSERT INTO experiment.text_resources (key, value) VALUES ('Notifications.CorrectAnswerProvided', 'Wygrałeś/aś zakład');
INSERT INTO experiment.text_resources (key, value) VALUES ('Notifications.NoCorrectAnswerProvided', 'Przegrałeś/aś zakład');

INSERT INTO experiment.text_resources (key, value) VALUES ('Instructions.TrialInstruction', 'Instrukcja do iteracji');
INSERT INTO experiment.text_resources (key, value) VALUES ('Instructions.QuestionnaireInstruction', 'Instrukcja do kwestionariusza');
INSERT INTO experiment.text_resources (key, value) VALUES ('Instructions.InitialInstruction', '
<p>Zapraszamy do udziału w badaniu finansowanym z grantu nr…., przyznanego przez Narodowe Centrum Nauki. Badanie jest w pełni anonimowe i służy wyłącznie do celów naukowych. Udział w badaniu jest dobrowolny. W każdej chwili możesz się wycofać bez podawania powodów. Jeśli wyrażasz zgodę na udział w badaniu, kliknij przycisk “dalej”.</p>
<p>Za chwilę na ekranie pojawi się lista 16 wskaźników makroekonomicznych i rynkowych. Wybierz 7 z nich, najważniejszych i wystarczających Twoim zdaniem do udzielenia odpowiedzi na pytanie: czy na rynku jest hossa czy bessa.</p>
');

INSERT INTO experiment.text_resources (key, value) VALUES ('Instructions.Lottery', '
W tej części badania masz możliwość wygrania dodatkowo 50 złotych.
Proponujemy Ci wybór między zakładem a loterią.
<p/>
Jeżeli wybierzesz zakład, wygrasz 50 złotych jeżeli trafnie odpowiedziałeś w jednym z dwóch ostatnich zadań - dziewiątym lub dziesiątym.
<p/>
Jeżeli wybierzesz loterię, możesz wygrać 50 złotych z prawdopodobieństwem %s.
<p/>
Niezależnie od tego co wybierzesz, to czy wygrasz dodatkowe 50 złotych będzie wiadome przed końcem komputerowej części badania.
');
INSERT INTO experiment.text_resources (key, value) VALUES ('Instructions.LotteryQuestion', 'Kliknij w jedno z dwóch okienek, zgodnie z wyborem, którego dokonałeś.');

INSERT INTO experiment.text_resources (key, value) VALUES ('Instructions.Thanks',
'
<h1>Dziękujemy za udział w badaniu</h1>
<p>W celu uzyskania wyników końcowych skontaktuj się z nami pod <a href="mailto:badanie@das-experiment.org">tym adresem</a>.</p>
<p>Tutaj można sobie dodać jakiś opis, może być kolorowy, zawierać obrazki, itd.
');

INSERT INTO experiment.text_resources (key, value) VALUES ('Instructions.VariablesSelection',
'
Wybierz 7 z nich, najważniejszych i wystarczających Twoim zdaniem do udzielenia odpowiedzi na pytanie:
czy na rynku jest hossa czy bessa.
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
100 punktów między wszystkie z nich tak, aby liczba przypisanych punktów odzwierciedlała ważność wskaźnika.
');

INSERT INTO experiment.text_resources (key, value) VALUES ('Instructions.Trial', '
Instrukcja iteracji
');

INSERT INTO experiment.text_resources (key, value) VALUES ('Instructions.ConfidenceQuestion',
'
Prosimy teraz o określenie stopnia pewności, że odpowiedziałeś prawidłowo na pytanie "Czy na rynku jest hossa czy bessa?" na skali 50%-100%.
Twoja odpowiedź może być dowolną liczbą z tego zakresu.
Wybór 50% oznacza, że strzelałeś, wybór 100% - że jesteś całkowicie pewien trafności swojej odpowiedzi.
');

INSERT INTO experiment.text_resources (key, value) VALUES ('Instructions.ExplanationQuestion',
'
W kilku słowach uzasadnij dlaczego uznałeś te wskaźniki za diagnostyczne.
');

INSERT INTO experiment.text_resources (key, value) VALUES ('Instructions.MostImportantVariables',
'
Wybierz te dwa wskaźniki, które miały największy wpływ na Twoją decyzję.
');

INSERT INTO experiment.text_resources (key, value) VALUES ('Instructions.Questionnaire',
'
Instrukcja do kwestionariusza
');

INSERT INTO experiment.text_resources (key, value) VALUES ('Instructions.PersonalData',
'
Instrukcja do ankiety
');
