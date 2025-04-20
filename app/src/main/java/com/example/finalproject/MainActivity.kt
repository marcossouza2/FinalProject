package com.example.finalproject

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.finalproject.dao.BookDao
import com.example.finalproject.dao.UserDao
import com.example.finalproject.data.AppDatabase
import com.example.finalproject.data.BookViewModel
import com.example.finalproject.data.UserViewModel
import com.example.finalproject.data.entities.Book
import com.example.finalproject.data.entities.User
import com.example.finalproject.ui.theme.FinalProjectTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var database: AppDatabase
    private lateinit var bookDao: BookDao
    private lateinit var userDao: UserDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        database = AppDatabase.getDatabase(applicationContext)
        bookDao = database.bookDao()
        userDao = database.userDao()

        setContent {
            FinalProjectTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = "login"
                ) {
                    composable("login") {
                        LoginScreenUI(
                            onLoginClick = {
                                navController.navigate("home")
                            },
                            onSignUpClick = {
                                navController.navigate("signup")
                            },
                            onForgotPasswordClick = { /* handle forgot password */ },
                            onNavigateToHome = {
                                navController.navigate("home")
                            }
                        )
                    }
                    composable("signup") {
                        val coroutineScope = rememberCoroutineScope()

                        SignUpScreen(
                            userDao,
                            onSignUpClick = { username, email, password, address, phone ->

                                val user = User(
                                    username = username,
                                    email = email,
                                    password = password,
                                    address = address,
                                    phone = phone
                                )
                                coroutineScope.launch {
                                    userDao.insertUser(user)
                                }
                                navController.navigate("login")
                            },
                            onNavigateToLogin = {
                                navController.navigate("login")
                            }
                        )
                    }
                    composable("home") {
                        HomeScreen(bookDao, navController)
                    }
                    composable("addBook") {
                        AddBookScreen(navController, userDao, bookDao, context = LocalContext.current)
                    }
                    composable("my_books") {
                        MyBooksScreen(bookDao = bookDao, navController = navController)
                    }
                }
            }
        }
    }
}


@Composable
fun LoginScreenUI(
    onLoginClick: () -> Unit,
    onSignUpClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    val context = LocalContext.current
    val userViewModel = UserViewModel(context.applicationContext as Application)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Log in", fontSize = 32.sp, style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        Text("Enter your email and password", fontSize = 16.sp)

        Spacer(modifier = Modifier.height(24.dp))

        Text("Email", style = MaterialTheme.typography.bodyLarge)
        TextField(
            value = email,
            onValueChange = { email = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Password", style = MaterialTheme.typography.bodyLarge)
        TextField(
            value = password,
            onValueChange = { password = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val icon = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = icon, contentDescription = "Toggle password visibility")
                }
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Forgot password?",
            modifier = Modifier
                .align(Alignment.End)
                .clickable(onClick = onForgotPasswordClick),
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {

                if (email.isNotEmpty() && password.isNotEmpty()) {

                    userViewModel.validateLogin(email, password) { user ->
                        if (user != null) {
                            saveUserEmail(context, user.email)
                            onNavigateToHome()

                        } else {
                            errorMessage = "Invalid credentials"
                            Log.e("LoginScreenUI", "Login failed for $email")
                        }
                    }
                } else {
                    errorMessage = "Please fill in both fields."
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Log In")
        }

        if (errorMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row {
            Text("Don't have an account? ")
            Text(
                text = "Sign up",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.clickable(onClick = onSignUpClick)
            )
        }
    }
}


@Composable
fun SignUpScreen(
    userDao: UserDao,
    onSignUpClick: (String, String, String, String, String) -> Unit,
    onNavigateToLogin: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Sign Up", fontSize = 32.sp, style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        Text("Enter your details to create an account", fontSize = 16.sp)

        Spacer(modifier = Modifier.height(24.dp))

        Text("Username", style = MaterialTheme.typography.bodyLarge)
        TextField(
            value = username,
            onValueChange = { username = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = { Text("Enter your username") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Email", style = MaterialTheme.typography.bodyLarge)
        TextField(
            value = email,
            onValueChange = { email = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = { Text("Enter your email") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Password", style = MaterialTheme.typography.bodyLarge)
        TextField(
            value = password,
            onValueChange = { password = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            placeholder = { Text("Enter your password") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Address", style = MaterialTheme.typography.bodyLarge)
        TextField(
            value = address,
            onValueChange = { address = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = { Text("Enter your address") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Phone Number", style = MaterialTheme.typography.bodyLarge)
        TextField(
            value = phone,
            onValueChange = { phone = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = { Text("Enter your phone number") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = errorMessage,
            color = Color.Red,
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (username.isEmpty() || email.isEmpty() || password.isEmpty() || address.isEmpty() || phone.isEmpty()) {
                    errorMessage = "Please fill all the fields"
                } else {
                    errorMessage = ""
                    val newUser = User(
                        username = username,
                        email = email,
                        password = password,
                        address = address,
                        phone = phone
                    )
                    scope.launch {
                        userDao.insertUser(newUser)
                        onNavigateToLogin()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Sign Up")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row {
            Text("Already have an account? ")
            Text(
                text = "Log in",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.clickable(onClick = onNavigateToLogin)
            )
        }
    }
}


@Composable
fun HomeScreen(bookDao: BookDao, navController: NavController) {
    val context = LocalContext.current
    val bookViewModel = BookViewModel(context.applicationContext as Application)

    val books by bookViewModel.allBooks.observeAsState(initial = emptyList())

    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Text("Find Books", style = MaterialTheme.typography.headlineMedium)

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search for a book") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            val filteredBooks = remember(books, searchQuery) {
                books.filter { it.title.contains(searchQuery, ignoreCase = true) }
            }

            LazyColumn {
                items(filteredBooks) { book ->
                    BookCard(book)
                }
            }
        }
    }
}



@Composable
fun BookCard(book: Book) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Title: ${book.title}", style = MaterialTheme.typography.titleMedium)
            Text("Author: ${book.author}")
            Text("Year: ${book.year}")
            Text("Genre: ${book.genre}")
            Text("Price: ${book.price}")
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    NavigationBar {
        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_home),
                    contentDescription = "Home",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text("Home") },
            selected = true,
            onClick = { navController.navigate("home") }
        )
        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_add_book),
                    contentDescription = "Add Book",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text("Add Book") },
            selected = false,
            onClick = { navController.navigate("addBook") }
        )
        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_my_books),
                    contentDescription = "My Books",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text("My Books") },
            selected = false,
            onClick = {
                navController.navigate("my_books") {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true }
            }
        )
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AddBookScreen(navController: NavController, userDao: UserDao, bookDao: BookDao, context: Context) {
    val currentUserEmail = getCurrentUserEmail(context)
    if (currentUserEmail == null) {
        navController.navigate("login")
        return
    }

    var author by remember { mutableStateOf(TextFieldValue()) }
    var title by remember { mutableStateOf(TextFieldValue()) }
    var year by remember { mutableStateOf(TextFieldValue()) }
    var genre by remember { mutableStateOf(TextFieldValue()) }
    var price by remember { mutableStateOf(TextFieldValue()) }

    var currentUser by remember { mutableStateOf<User?>(null) }

    val scope = rememberCoroutineScope()

    LaunchedEffect(currentUserEmail) {
        currentUser = userDao.getUserByEmail(currentUserEmail)
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(100.dp))

            Text(text = "Post a Book", style = MaterialTheme.typography.headlineLarge)

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Author")
            OutlinedTextField(
                value = author,
                onValueChange = { author = it },
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                textStyle = MaterialTheme.typography.bodyMedium,
                label = { Text("Enter Author Name") }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "Title")
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                textStyle = MaterialTheme.typography.bodyMedium,
                label = { Text("Enter Book Title") }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "Year")
            OutlinedTextField(
                value = year,
                onValueChange = { year = it },
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                textStyle = MaterialTheme.typography.bodyMedium,
                label = { Text("Enter Release Year") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "Genre")
            OutlinedTextField(
                value = genre,
                onValueChange = { genre = it },
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                textStyle = MaterialTheme.typography.bodyMedium,
                label = { Text("Enter Genre") }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "Price")
            OutlinedTextField(
                value = price,
                onValueChange = { price = it },
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                textStyle = MaterialTheme.typography.bodyMedium,
                label = { Text("Enter Price") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val book = Book(
                        author = author.text,
                        title = title.text,
                        year = year.text.toIntOrNull() ?: 0,
                        genre = genre.text,
                        price = price.text.toDoubleOrNull() ?: 0.0,
                        email = currentUserEmail
                    )
                    scope.launch {
                        bookDao.insert(book)
                        navController.navigate("home")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Post")
            }
        }
    }
}



fun saveUserEmail(context: Context, email: String) {
    val sharedPref = context.getSharedPreferences("user_data", Context.MODE_PRIVATE)
    with(sharedPref.edit()) {
        putString("current_user_email", email)
        apply()
    }
}

fun getCurrentUserEmail(context: Context): String? {
    val sharedPref = context.getSharedPreferences("user_data", Context.MODE_PRIVATE)
    return sharedPref.getString("current_user_email", null)
}

@Composable
fun MyBooksScreen(bookDao: BookDao, navController: NavController) {
    val context = LocalContext.current
    val bookViewModel = BookViewModel(context.applicationContext as Application)

    val books by bookViewModel.allBooks.observeAsState(initial = emptyList())

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Text("My Books", style = MaterialTheme.typography.headlineMedium)

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn {
                items(books) { book ->
                    BookCard(book)
                }
            }
        }
    }
}