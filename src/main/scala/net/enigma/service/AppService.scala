package net.enigma.service

/**
 * @author Jacek Lewandowski
 */
trait AppService
  extends LoginService
  with PersonalDataService
  with QuestionnaireService
  with ExperimentService
  with UserService {

}
