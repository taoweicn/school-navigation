import com.google.common.base.CaseFormat;

public enum Type {
  /** 人文景点 */
  CULTURAL_ATTRACTION,
  /** 教室 */
  CLASSROOM,
  /** 宿舍 */
  DORMITORY;

  public static Type mapStringToType(String name) {
    return Enum.valueOf(Type.class, CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, name));
  }
}
