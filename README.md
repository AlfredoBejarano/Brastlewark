# Brastlewark

Android app that displays a list of the gome inhabitants of the Brastlewark village.

This app holds an interesting code challenge as the challenge is create the app **without any external libraries for basic functionality**.
this means, things that we as android developers take for granted such as Retrofit, Volley, GSON, Dagger or any android Jetpack libraries, such as
RecyclerView, ConstraintLayout, and Lifecycle extensions are **not available**.

Only libraries for extended functionality are allowed, such as Mockito and Robolectric.

# App architecture.

The app attempts to use the following architecture:

![arch](https://developer.android.com/topic/libraries/architecture/images/final-architecture.png)

## Model
Model classes helps to encapsulate data from various data sources in a easily usable Kotiln object.

Model classes:
- Gnome: Contains the data of a gnome, such as name or profile picture.

## Datasource

Datasource classes are the one responsible for providing data to the repository classes, the data source classes can be
classified in two categories, **local** and **remote** data sources. Local data sources provide data from the device
itself, while remote data sources use sources outside the phone, like the internet.

Datasource classes:
- BrastlewarkDatabase (local): Provides access to the app local database
- CachedPhotoDataBaseHelper (local): Provides an easier access to just the cache archives of photos, instead of accessing to the database itself.
- GnomeDataBaseHelper (local): Provides an easier access to just the gnome model records, instead of accessing to the database itself.
- SharedPreferencesDataSource (local): Provides direct access to keys in the shared preferences file, instead of accessing SharedPreferences classes directly.
- GnomeApiService (remote): Provides access to remote resources, such as the list of gnomes inhabitants or images.

## Repository

Repository classes helps to provide a single source of truth for the app, unifying the various data source classes
into one. This class decides from which data source class provide the requested information.

Repository classes:
- GnomeRepository: Repository class that provides data related to gnomes.
- CachedPhotoRepository: Repository class that provides data related to the photos cache archives.

## ViewModel

"Smart" classes attached to the life cycle of a lifecycle owner that provide observation to the UI for data
requested by it using repository classes. As repository classes work is usually done in a worker thread, this
job can take a while, instead of freezing the UI, the ViewModel notifies the UI when the data is retrieved.
ViewModel classes react to UI events such as button clicks or EditText's value changes.

ViewModel classes:
- GnomeDetailsViewModel: ViewModel class that provides UI observation for a Lifecycle owner that requests the details of a gnome by its name.
- GnomeListViewModel: ViewModel class that provides UI observation for a Lifecycle owner that requests a list of gnomes and provide filters to sort said list.

## UI

Classes that provide communication within the user and the app itself, this classes display data to the users and
intercept their interactions within the app. this class **are usually** lifecycle owners, (but not always).

UI classes:
- FiltersAlertDialog: AlertDialog class that shows the available filters to the user that allows the users to sort the gnomes list.
- GnomeActivity: Activity class that displays the details of a single gnome.
- MainActivity: The first screen in the app. This activity displays the list of gnomes inhabitants.

## Miscellaneous
- Utils: Extension functions that help reduce boilerplate code and simplify complex functions used across the app.
- Events: Kotlin object that provides observations for any exception happening in the app.

# Conclusion

Even though this code challenge provides a nice throwback as how the life of an android developer life was
around 2008 (without counting the clunky usage of Eclipse and that ViewModels are being used),
the development of this app could have been faster if libraries such as Dagger, Retrofit, GSON, Room or
even Google Material libraries were available to use.
