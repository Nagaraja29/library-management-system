package com.library.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.config.SupabaseConfig;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class SupabaseService {

    private final OkHttpClient client;
    private final ObjectMapper objectMapper;

    public SupabaseService() {

        client = new OkHttpClient();
        objectMapper = new ObjectMapper();
    }

    public void testConnection() {

        String url = SupabaseConfig.SUPABASE_URL
                + "/rest/v1/users?select=id,name,email,role";

        Request request = new Request.Builder()
                .url(url)
                .addHeader("apikey", SupabaseConfig.SUPABASE_KEY)
                .addHeader(
                        "Authorization",
                        "Bearer " + SupabaseConfig.SUPABASE_KEY
                )
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {

            System.out.println("Status Code: " + response.code());

            if (response.body() != null) {

                System.out.println("Supabase Response:");
                System.out.println(
                        response.body().string()
                );
            }

        } catch (IOException e) {

            System.out.println(
                    "Supabase connection failed!"
            );

            e.printStackTrace();
        }
    }

    public String login(String email, String password)
            throws IOException {

        HttpUrl url = HttpUrl.parse(
                SupabaseConfig.SUPABASE_URL
                        + "/rest/v1/users"
        ).newBuilder()
                .addQueryParameter(
                        "select",
                        "role"
                )
                .addQueryParameter(
                        "email",
                        "eq." + email
                )
                .addQueryParameter(
                        "password",
                        "eq." + password
                )
                .build();

        Request request = new Request.Builder()
                .url(url)
                .addHeader(
                        "apikey",
                        SupabaseConfig.SUPABASE_KEY
                )
                .addHeader(
                        "Authorization",
                        "Bearer "
                                + SupabaseConfig.SUPABASE_KEY
                )
                .get()
                .build();

        try (Response response =
                     client.newCall(request).execute()) {

            if (!response.isSuccessful()) {

                throw new IOException(
                        "Supabase error: "
                                + response.code()
                );
            }

            if (response.body() == null) {
                return null;
            }

            String responseBody =
                    response.body().string();

            JsonNode json =
                    objectMapper.readTree(responseBody);

            if (!json.isArray()
                    || json.isEmpty()) {

                return null;
            }

            return json.get(0)
                    .get("role")
                    .asText();
        
        }
    }
    public String searchBooks(String searchText) throws IOException {

    HttpUrl url = HttpUrl.parse(
            SupabaseConfig.SUPABASE_URL
                    + "/rest/v1/books"
    ).newBuilder()
            .addQueryParameter(
                    "select",
                    "book_id,title,author,isbn,category,quantity,available_quantity"
            )
            .addQueryParameter(
                    "or",
                    "(book_id.ilike.*" + searchText
                            + "*,title.ilike.*" + searchText + "*)"
            )
            .build();

    Request request = new Request.Builder()
            .url(url)
            .addHeader(
                    "apikey",
                    SupabaseConfig.SUPABASE_KEY
            )
            .addHeader(
                    "Authorization",
                    "Bearer " + SupabaseConfig.SUPABASE_KEY
            )
            .get()
            .build();

    try (Response response =
                 client.newCall(request).execute()) {

        if (!response.isSuccessful()) {

            throw new IOException(
                    "Supabase error: "
                            + response.code()
            );
        }

        if (response.body() == null) {
            return "[]";
        }

        return response.body().string();
    }
}
public boolean addBook(
        String bookId,
        String title,
        String author,
        String isbn,
        String category,
        int quantity
) throws IOException {

    String url =
            SupabaseConfig.SUPABASE_URL
                    + "/rest/v1/books";

    String json = """
            {
                "book_id": "%s",
                "title": "%s",
                "author": "%s",
                "isbn": "%s",
                "category": "%s",
                "quantity": %d,
                "available_quantity": %d
            }
            """.formatted(
                    bookId,
                    title,
                    author,
                    isbn,
                    category,
                    quantity,
                    quantity
            );

    okhttp3.RequestBody body =
            okhttp3.RequestBody.create(
                    json,
                    okhttp3.MediaType.parse(
                            "application/json"
                    )
            );

    Request request = new Request.Builder()
            .url(url)
            .addHeader(
                    "apikey",
                    SupabaseConfig.SUPABASE_KEY
            )
            .addHeader(
                    "Authorization",
                    "Bearer "
                            + SupabaseConfig.SUPABASE_KEY
            )
            .addHeader(
                    "Content-Type",
                    "application/json"
            )
            .addHeader(
                    "Prefer",
                    "return=minimal"
            )
            .post(body)
            .build();

    try (Response response =
                 client.newCall(request).execute()) {

        System.out.println(
                "Add Book Status: "
                        + response.code()
        );

        return response.isSuccessful();
    }
}
public boolean upgradeBookQuantity(
        String bookId,
        int additionalQuantity
) throws IOException {

    // First find the book
    HttpUrl findUrl = HttpUrl.parse(
            SupabaseConfig.SUPABASE_URL
                    + "/rest/v1/books"
    ).newBuilder()
            .addQueryParameter(
                    "select",
                    "quantity,available_quantity"
            )
            .addQueryParameter(
                    "book_id",
                    "eq." + bookId
            )
            .build();

    Request findRequest = new Request.Builder()
            .url(findUrl)
            .addHeader(
                    "apikey",
                    SupabaseConfig.SUPABASE_KEY
            )
            .addHeader(
                    "Authorization",
                    "Bearer "
                            + SupabaseConfig.SUPABASE_KEY
            )
            .get()
            .build();

    int currentQuantity;
    int currentAvailableQuantity;

    try (Response response =
                 client.newCall(findRequest).execute()) {

        if (!response.isSuccessful()) {
            throw new IOException(
                    "Failed to find book. Status: "
                            + response.code()
            );
        }

        if (response.body() == null) {
            return false;
        }

        String responseBody =
                response.body().string();

        JsonNode books =
                objectMapper.readTree(responseBody);

        if (!books.isArray()
                || books.isEmpty()) {

            return false;
        }

        JsonNode book = books.get(0);

        currentQuantity =
                book.get("quantity").asInt();

        currentAvailableQuantity =
                book.get("available_quantity").asInt();
    }

    // Calculate new quantities
    int newQuantity =
            currentQuantity + additionalQuantity;

    int newAvailableQuantity =
            currentAvailableQuantity
                    + additionalQuantity;

    // Update database
    String updateUrl =
            SupabaseConfig.SUPABASE_URL
                    + "/rest/v1/books?book_id=eq."
                    + bookId;

    String json = """
            {
                "quantity": %d,
                "available_quantity": %d
            }
            """.formatted(
                    newQuantity,
                    newAvailableQuantity
            );

    okhttp3.RequestBody body =
            okhttp3.RequestBody.create(
                    json,
                    okhttp3.MediaType.parse(
                            "application/json"
                    )
            );

    Request updateRequest =
            new Request.Builder()
                    .url(updateUrl)
                    .addHeader(
                            "apikey",
                            SupabaseConfig.SUPABASE_KEY
                    )
                    .addHeader(
                            "Authorization",
                            "Bearer "
                                    + SupabaseConfig.SUPABASE_KEY
                    )
                    .addHeader(
                            "Content-Type",
                            "application/json"
                    )
                    .addHeader(
                            "Prefer",
                            "return=minimal"
                    )
                    .patch(body)
                    .build();

    try (Response response =
                 client.newCall(updateRequest).execute()) {

        System.out.println(
                "Upgrade Quantity Status: "
                        + response.code()
        );

        return response.isSuccessful();
    }
}
public boolean registerStudent(
        String name,
        String registerNumber,
        String email,
        String password,
        String department,
        String phone
) throws IOException {

    String url =
            SupabaseConfig.SUPABASE_URL
                    + "/rest/v1/users";

    String json = """
            {
                "name": "%s",
                "register_number": "%s",
                "email": "%s",
                "password": "%s",
                "role": "STUDENT",
                "department": "%s",
                "phone": "%s"
            }
            """.formatted(
                    name,
                    registerNumber,
                    email,
                    password,
                    department,
                    phone
            );

    okhttp3.RequestBody body =
            okhttp3.RequestBody.create(
                    json,
                    okhttp3.MediaType.parse(
                            "application/json"
                    )
            );

    Request request =
            new Request.Builder()
                    .url(url)
                    .addHeader(
                            "apikey",
                            SupabaseConfig.SUPABASE_KEY
                    )
                    .addHeader(
                            "Authorization",
                            "Bearer "
                                    + SupabaseConfig.SUPABASE_KEY
                    )
                    .addHeader(
                            "Content-Type",
                            "application/json"
                    )
                    .addHeader(
                            "Prefer",
                            "return=minimal"
                    )
                    .post(body)
                    .build();

    try (Response response =
                 client.newCall(request).execute()) {

        System.out.println(
                "Register Student Status: "
                        + response.code()
        );

        return response.isSuccessful();
    }
}
public String getAllStudents() throws IOException {

    HttpUrl url = HttpUrl.parse(
            SupabaseConfig.SUPABASE_URL
                    + "/rest/v1/users"
    ).newBuilder()
            .addQueryParameter(
                    "select",
                    "name,register_number,email,department,phone"
            )
            .addQueryParameter(
                    "role",
                    "eq.STUDENT"
            )
            .addQueryParameter(
                    "order",
                    "name.asc"
            )
            .build();

    Request request = new Request.Builder()
            .url(url)
            .addHeader(
                    "apikey",
                    SupabaseConfig.SUPABASE_KEY
            )
            .addHeader(
                    "Authorization",
                    "Bearer " + SupabaseConfig.SUPABASE_KEY
            )
            .get()
            .build();

    try (Response response =
                 client.newCall(request).execute()) {

        System.out.println(
                "Get Students Status: "
                        + response.code()
        );

        if (!response.isSuccessful()) {
            throw new IOException(
                    "Supabase error: "
                            + response.code()
            );
        }

        if (response.body() == null) {
            return "[]";
        }

        return response.body().string();
    }
}
public boolean checkInBook(
        String bookId,
        String studentRegisterNumber
) throws IOException {

    // 1. Find the book
    HttpUrl bookUrl = HttpUrl.parse(
            SupabaseConfig.SUPABASE_URL
                    + "/rest/v1/books"
    ).newBuilder()
            .addQueryParameter(
                    "select",
                    "id,available_quantity"
            )
            .addQueryParameter(
                    "book_id",
                    "eq." + bookId
            )
            .build();

    Request bookRequest = new Request.Builder()
            .url(bookUrl)
            .addHeader(
                    "apikey",
                    SupabaseConfig.SUPABASE_KEY
            )
            .addHeader(
                    "Authorization",
                    "Bearer " + SupabaseConfig.SUPABASE_KEY
            )
            .get()
            .build();

    long databaseBookId;
    int availableQuantity;

    try (Response response =
                 client.newCall(bookRequest).execute()) {

        if (!response.isSuccessful()) {
            throw new IOException(
                    "Failed to find book. Status: "
                            + response.code()
            );
        }

        if (response.body() == null) {
            return false;
        }

        String responseBody =
                response.body().string();

        JsonNode books =
                objectMapper.readTree(responseBody);

        if (!books.isArray()
                || books.isEmpty()) {
            return false;
        }

        JsonNode book = books.get(0);

        databaseBookId =
                book.get("id").asLong();

        availableQuantity =
                book.get("available_quantity").asInt();
    }

    // 2. Check availability
    if (availableQuantity <= 0) {
        return false;
    }

    // 3. Find student
    HttpUrl studentUrl = HttpUrl.parse(
            SupabaseConfig.SUPABASE_URL
                    + "/rest/v1/users"
    ).newBuilder()
            .addQueryParameter(
                    "select",
                    "id"
            )
            .addQueryParameter(
                    "register_number",
                    "eq." + studentRegisterNumber
            )
            .addQueryParameter(
                    "role",
                    "eq.STUDENT"
            )
            .build();

    Request studentRequest =
            new Request.Builder()
                    .url(studentUrl)
                    .addHeader(
                            "apikey",
                            SupabaseConfig.SUPABASE_KEY
                    )
                    .addHeader(
                            "Authorization",
                            "Bearer "
                                    + SupabaseConfig.SUPABASE_KEY
                    )
                    .get()
                    .build();

    long studentId;

    try (Response response =
                 client.newCall(studentRequest).execute()) {

        if (!response.isSuccessful()) {
            throw new IOException(
                    "Failed to find student. Status: "
                            + response.code()
            );
        }

        if (response.body() == null) {
            return false;
        }

        String responseBody =
                response.body().string();

        JsonNode students =
                objectMapper.readTree(responseBody);

        if (!students.isArray()
                || students.isEmpty()) {
            return false;
        }

        studentId =
                students.get(0)
                        .get("id")
                        .asLong();
    }

    // 4. Calculate due date
    java.time.OffsetDateTime now =
            java.time.OffsetDateTime.now();

    java.time.OffsetDateTime dueDate =
            now.plusDays(14);

    // 5. Create borrowing record
    String borrowingUrl =
            SupabaseConfig.SUPABASE_URL
                    + "/rest/v1/borrowings";

    String json = """
            {
                "book_id": %d,
                "student_id": %d,
                "issue_date": "%s",
                "due_date": "%s",
                "status": "BORROWED",
                "fine": 0
            }
            """.formatted(
                    databaseBookId,
                    studentId,
                    now,
                    dueDate
            );

    okhttp3.RequestBody body =
            okhttp3.RequestBody.create(
                    json,
                    okhttp3.MediaType.parse(
                            "application/json"
                    )
            );

    Request borrowingRequest =
            new Request.Builder()
                    .url(borrowingUrl)
                    .addHeader(
                            "apikey",
                            SupabaseConfig.SUPABASE_KEY
                    )
                    .addHeader(
                            "Authorization",
                            "Bearer "
                                    + SupabaseConfig.SUPABASE_KEY
                    )
                    .addHeader(
                            "Content-Type",
                            "application/json"
                    )
                    .addHeader(
                            "Prefer",
                            "return=minimal"
                    )
                    .post(body)
                    .build();

    try (Response response =
                 client.newCall(borrowingRequest).execute()) {

        if (!response.isSuccessful()) {
            return false;
        }
    }

    // 6. Decrease available quantity
    String updateUrl =
            SupabaseConfig.SUPABASE_URL
                    + "/rest/v1/books?book_id=eq."
                    + bookId;

    String updateJson = """
            {
                "available_quantity": %d
            }
            """.formatted(
                    availableQuantity - 1
            );

    okhttp3.RequestBody updateBody =
            okhttp3.RequestBody.create(
                    updateJson,
                    okhttp3.MediaType.parse(
                            "application/json"
                    )
            );

    Request updateRequest =
            new Request.Builder()
                    .url(updateUrl)
                    .addHeader(
                            "apikey",
                            SupabaseConfig.SUPABASE_KEY
                    )
                    .addHeader(
                            "Authorization",
                            "Bearer "
                                    + SupabaseConfig.SUPABASE_KEY
                    )
                    .addHeader(
                            "Content-Type",
                            "application/json"
                    )
                    .addHeader(
                            "Prefer",
                            "return=minimal"
                    )
                    .patch(updateBody)
                    .build();

    try (Response response =
                 client.newCall(updateRequest).execute()) {

        return response.isSuccessful();
    }
}   
public boolean checkOutBook(
        String bookId,
        String studentRegisterNumber
) throws IOException {

    // 1. Find the database book ID
    HttpUrl bookUrl = HttpUrl.parse(
            SupabaseConfig.SUPABASE_URL
                    + "/rest/v1/books"
    ).newBuilder()
            .addQueryParameter("select", "id")
            .addQueryParameter(
                    "book_id",
                    "eq." + bookId
            )
            .build();

    Request bookRequest = new Request.Builder()
            .url(bookUrl)
            .addHeader(
                    "apikey",
                    SupabaseConfig.SUPABASE_KEY
            )
            .addHeader(
                    "Authorization",
                    "Bearer " + SupabaseConfig.SUPABASE_KEY
            )
            .get()
            .build();

    long databaseBookId;

    try (Response response =
                 client.newCall(bookRequest).execute()) {

        if (!response.isSuccessful()
                || response.body() == null) {
            return false;
        }

        JsonNode books =
                objectMapper.readTree(
                        response.body().string()
                );

        if (!books.isArray()
                || books.isEmpty()) {
            return false;
        }

        databaseBookId =
                books.get(0).get("id").asLong();
    }

    // 2. Find the student ID
    HttpUrl studentUrl = HttpUrl.parse(
            SupabaseConfig.SUPABASE_URL
                    + "/rest/v1/users"
    ).newBuilder()
            .addQueryParameter("select", "id")
            .addQueryParameter(
                    "register_number",
                    "eq." + studentRegisterNumber
            )
            .addQueryParameter(
                    "role",
                    "eq.STUDENT"
            )
            .build();

    Request studentRequest =
            new Request.Builder()
                    .url(studentUrl)
                    .addHeader(
                            "apikey",
                            SupabaseConfig.SUPABASE_KEY
                    )
                    .addHeader(
                            "Authorization",
                            "Bearer "
                                    + SupabaseConfig.SUPABASE_KEY
                    )
                    .get()
                    .build();

    long studentId;

    try (Response response =
                 client.newCall(studentRequest).execute()) {

        if (!response.isSuccessful()
                || response.body() == null) {
            return false;
        }

        JsonNode students =
                objectMapper.readTree(
                        response.body().string()
                );

        if (!students.isArray()
                || students.isEmpty()) {
            return false;
        }

        studentId =
                students.get(0).get("id").asLong();
    }

    // 3. Find active borrowing
    HttpUrl borrowingUrl = HttpUrl.parse(
            SupabaseConfig.SUPABASE_URL
                    + "/rest/v1/borrowings"
    ).newBuilder()
            .addQueryParameter(
                    "select",
                    "id"
            )
            .addQueryParameter(
                    "book_id",
                    "eq." + databaseBookId
            )
            .addQueryParameter(
                    "student_id",
                    "eq." + studentId
            )
            .addQueryParameter(
                    "status",
                    "eq.BORROWED"
            )
            .build();

    Request borrowingRequest =
            new Request.Builder()
                    .url(borrowingUrl)
                    .addHeader(
                            "apikey",
                            SupabaseConfig.SUPABASE_KEY
                    )
                    .addHeader(
                            "Authorization",
                            "Bearer "
                                    + SupabaseConfig.SUPABASE_KEY
                    )
                    .get()
                    .build();

    long borrowingId;

    try (Response response =
                 client.newCall(borrowingRequest).execute()) {

        if (!response.isSuccessful()
                || response.body() == null) {
            return false;
        }

        JsonNode borrowings =
                objectMapper.readTree(
                        response.body().string()
                );

        if (!borrowings.isArray()
                || borrowings.isEmpty()) {
            return false;
        }

        borrowingId =
                borrowings.get(0)
                        .get("id")
                        .asLong();
    }

    // 4. Mark borrowing as returned
    String returnUrl =
            SupabaseConfig.SUPABASE_URL
                    + "/rest/v1/borrowings?id=eq."
                    + borrowingId;

    String returnJson = """
            {
                "return_date": "%s",
                "status": "RETURNED",
                "fine": 0
            }
            """.formatted(
                    java.time.OffsetDateTime.now()
            );

    okhttp3.RequestBody returnBody =
            okhttp3.RequestBody.create(
                    returnJson,
                    okhttp3.MediaType.parse(
                            "application/json"
                    )
            );

    Request returnRequest =
            new Request.Builder()
                    .url(returnUrl)
                    .addHeader(
                            "apikey",
                            SupabaseConfig.SUPABASE_KEY
                    )
                    .addHeader(
                            "Authorization",
                            "Bearer "
                                    + SupabaseConfig.SUPABASE_KEY
                    )
                    .addHeader(
                            "Content-Type",
                            "application/json"
                    )
                    .addHeader(
                            "Prefer",
                            "return=minimal"
                    )
                    .patch(returnBody)
                    .build();

    try (Response response =
                 client.newCall(returnRequest).execute()) {

        if (!response.isSuccessful()) {
            return false;
        }
    }

    // 5. Get current available quantity
    HttpUrl quantityUrl = HttpUrl.parse(
            SupabaseConfig.SUPABASE_URL
                    + "/rest/v1/books"
    ).newBuilder()
            .addQueryParameter(
                    "select",
                    "available_quantity"
            )
            .addQueryParameter(
                    "id",
                    "eq." + databaseBookId
            )
            .build();

    Request quantityRequest =
            new Request.Builder()
                    .url(quantityUrl)
                    .addHeader(
                            "apikey",
                            SupabaseConfig.SUPABASE_KEY
                    )
                    .addHeader(
                            "Authorization",
                            "Bearer " + SupabaseConfig.SUPABASE_KEY
                    )
                    .get()
                    .build();

    int availableQuantity;

    try (Response response =
                 client.newCall(quantityRequest).execute()) {

        if (!response.isSuccessful()
                || response.body() == null) {
            return false;
        }

        JsonNode books =
                objectMapper.readTree(
                        response.body().string()
                );

        if (!books.isArray()
                || books.isEmpty()) {
            return false;
        }

        availableQuantity =
                books.get(0)
                        .get("available_quantity")
                        .asInt();
    }

    // 6. Increase available quantity
    String updateUrl =
            SupabaseConfig.SUPABASE_URL
                    + "/rest/v1/books?id=eq."
                    + databaseBookId;

    String updateJson = """
            {
                "available_quantity": %d
            }
            """.formatted(
                    availableQuantity + 1
            );

    okhttp3.RequestBody updateBody =
            okhttp3.RequestBody.create(
                    updateJson,
                    okhttp3.MediaType.parse(
                            "application/json"
                    )
            );

    Request updateRequest =
            new Request.Builder()
                    .url(updateUrl)
                    .addHeader(
                            "apikey",
                            SupabaseConfig.SUPABASE_KEY
                    )
                    .addHeader(
                            "Authorization",
                            "Bearer " + SupabaseConfig.SUPABASE_KEY
                    )
                    .addHeader(
                            "Content-Type",
                            "application/json"
                    )
                    .addHeader(
                            "Prefer",
                            "return=minimal"
                    )
                    .patch(updateBody)
                    .build();

    try (Response response =
                 client.newCall(updateRequest).execute()) {

        return response.isSuccessful();
    }
}
public String getStudentBorrowings(
        String registerNumber
) throws IOException {

    // Find student ID
    HttpUrl studentUrl = HttpUrl.parse(
            SupabaseConfig.SUPABASE_URL
                    + "/rest/v1/users"
    ).newBuilder()
            .addQueryParameter(
                    "select",
                    "id"
            )
            .addQueryParameter(
                    "register_number",
                    "eq." + registerNumber
            )
            .build();

    Request studentRequest =
            new Request.Builder()
                    .url(studentUrl)
                    .addHeader(
                            "apikey",
                            SupabaseConfig.SUPABASE_KEY
                    )
                    .addHeader(
                            "Authorization",
                            "Bearer "
                                    + SupabaseConfig.SUPABASE_KEY
                    )
                    .get()
                    .build();

    long studentId;

    try (Response response =
                 client.newCall(studentRequest).execute()) {

        if (!response.isSuccessful()
                || response.body() == null) {
            throw new IOException(
                    "Failed to find student."
            );
        }

        JsonNode students =
                objectMapper.readTree(
                        response.body().string()
                );

        if (!students.isArray()
                || students.isEmpty()) {
            return "[]";
        }

        studentId =
                students.get(0)
                        .get("id")
                        .asLong();
    }

    // Get borrowing history
    HttpUrl borrowingUrl = HttpUrl.parse(
            SupabaseConfig.SUPABASE_URL
                    + "/rest/v1/borrowings"
    ).newBuilder()
            .addQueryParameter(
                    "select",
                    "id,book_id,issue_date,due_date,"
                            + "return_date,status,fine,"
                            + "books(book_id,title)"
            )
            .addQueryParameter(
                    "student_id",
                    "eq." + studentId
            )
            .addQueryParameter(
                    "order",
                    "issue_date.desc"
            )
            .build();

    Request borrowingRequest =
            new Request.Builder()
                    .url(borrowingUrl)
                    .addHeader(
                            "apikey",
                            SupabaseConfig.SUPABASE_KEY
                    )
                    .addHeader(
                            "Authorization",
                            "Bearer "
                                    + SupabaseConfig.SUPABASE_KEY
                    )
                    .get()
                    .build();

    try (Response response =
                 client.newCall(borrowingRequest).execute()) {

        if (!response.isSuccessful()) {
            throw new IOException(
                    "Failed to get borrowing history. Status: "
                            + response.code()
            );
        }

        if (response.body() == null) {
            return "[]";
        }

        return response.body().string();
    }
}
}