package task.omer.businesscard.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import java.util.Objects;

public class Person implements Parcelable {
    private String photo;
    private String fullName;
    private String phone;
    private String email;
    private String company;
    private String job;
    private LatLng location;

    public Person(String photo, String fullName, String phone, String email, String company, String job, LatLng location) {
        this.photo = photo;
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.company = company;
        this.job = job;
        this.location = location;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    protected Person(Parcel in) {
        photo = in.readString();
        fullName = in.readString();
        phone = in.readString();
        email = in.readString();
        company = in.readString();
        job = in.readString();
        location = in.readParcelable(LatLng.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(photo);
        dest.writeString(fullName);
        dest.writeString(phone);
        dest.writeString(email);
        dest.writeString(company);
        dest.writeString(job);
        dest.writeParcelable(location, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Person> CREATOR = new Creator<Person>() {
        @Override
        public Person createFromParcel(Parcel in) {
            return new Person(in);
        }

        @Override
        public Person[] newArray(int size) {
            return new Person[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Person)) return false;
        Person person = (Person) o;
        return Objects.equals(photo, person.photo) &&
                Objects.equals(fullName, person.fullName) &&
                Objects.equals(phone, person.phone) &&
                Objects.equals(email, person.email) &&
                Objects.equals(company, person.company) &&
                Objects.equals(job, person.job) &&
                Objects.equals(location, person.location);
    }
}
