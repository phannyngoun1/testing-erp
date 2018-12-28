package net.dream.erp.domain.User

import net.dream.erp.domain.{Address, Contact}
import net.dream.erp.domain.User.User.Profile

object User {

  case class Profile(firstName: String, lastName: String, gender: String)

  val admin = User(1, "admin", "Administrator")
}

case class User(
  id: Int,
  loginName: String,
  displayName: String,
  profile: Option[Profile] = None,
  addresses: Option[List[Address]] = None,
  contacts: Option[List[Contact]] = None
)




