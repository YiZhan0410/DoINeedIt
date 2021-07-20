package YiZhan.Wong.doineedit;

public class AccountInfo {

    // Instantiate the variables for firstName, surname and gender
    private String firstName;
    private String surname;
    private String gender;

    // empty constructor
    public AccountInfo(){

    }

    // constructor for AccountInfo
    public AccountInfo(String FirstName, String Surname, String Gender){
        this.firstName = FirstName;
        this.surname = Surname;
        this.gender = Gender;
    }

    // Method to get FirstName
    public String getFirstName() { return firstName; }

    // Method to Surname
    public String getSurname() { return surname; }

    // Method to get Gender
    public String getGender() { return gender; }
}
