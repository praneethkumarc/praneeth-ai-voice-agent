package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandSecondary
import com.example.ui.theme.Slate500

enum class AuthMode {
  LOGIN,
  REGISTER,
  FORGOT_PASSWORD,
  EMAIL_VERIFICATION
}

@Composable
fun AuthScreen(
  onAuthenticated: () -> Unit,
  modifier: Modifier = Modifier
) {
  var mode by remember { mutableStateOf(AuthMode.LOGIN) }
  var email by remember { mutableStateOf("praneethangel777@gmail.com") }
  var password by remember { mutableStateOf("password123") }
  var fullName by remember { mutableStateOf("Praneeth Kumar") }
  var businessName by remember { mutableStateOf("Praneeth AI Employee") }
  var phone by remember { mutableStateOf("8951858777") }
  var passwordVisible by remember { mutableStateOf(false) }
  var statusMessage by remember { mutableStateOf<String?>(null) }

  Surface(
    modifier = modifier.fillMaxSize(),
    color = MaterialTheme.colorScheme.background
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 24.dp, vertical = 32.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      // Branding Header
      Box(
        modifier = Modifier
          .size(64.dp)
          .clip(RoundedCornerShape(18.dp))
          .background(BrandPrimary),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Filled.SmartToy,
          contentDescription = null,
          tint = Color.White,
          modifier = Modifier.size(36.dp)
        )
      }

      Spacer(modifier = Modifier.height(16.dp))

      Text(
        text = "Praneeth AI Employee",
        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
      )

      Text(
        text = "Enterprise AI Voice Agent & Telephony Platform",
        style = MaterialTheme.typography.bodySmall.copy(
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          textAlign = TextAlign.Center
        ),
        modifier = Modifier.padding(top = 4.dp)
      )

      Spacer(modifier = Modifier.height(28.dp))

      // Auth Card
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .widthIn(max = 440.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          when (mode) {
            AuthMode.LOGIN -> {
              Text(
                text = "Welcome Back",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
              )
              Text(
                text = "Sign in to manage your AI employees & customer calls",
                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
              )

              OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Business Email") },
                leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("auth_email_input"),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
              )

              OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null) },
                trailingIcon = {
                  IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                      imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                      contentDescription = null
                    )
                  }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("auth_password_input")
              )

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
              ) {
                Text(
                  text = "Forgot password?",
                  style = MaterialTheme.typography.labelMedium.copy(
                    color = BrandPrimary,
                    fontWeight = FontWeight.SemiBold
                  ),
                  modifier = Modifier
                    .clickable { mode = AuthMode.FORGOT_PASSWORD }
                    .testTag("forgot_password_button")
                )
              }

              Button(
                onClick = { onAuthenticated() },
                modifier = Modifier
                  .fillMaxWidth()
                  .height(48.dp)
                  .testTag("login_submit_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
              ) {
                Text("Sign In to Dashboard", fontWeight = FontWeight.Bold)
              }

              // Google Login Simulation
              OutlinedButton(
                onClick = { onAuthenticated() },
                modifier = Modifier
                  .fillMaxWidth()
                  .height(48.dp)
                  .testTag("google_login_button"),
                shape = RoundedCornerShape(12.dp)
              ) {
                Icon(Icons.Filled.AccountCircle, contentDescription = null, tint = BrandPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Continue with Google", fontWeight = FontWeight.SemiBold)
              }

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text("Don't have an account? ", style = MaterialTheme.typography.bodySmall)
                Text(
                  "Create Account",
                  style = MaterialTheme.typography.bodySmall.copy(
                    color = BrandPrimary,
                    fontWeight = FontWeight.Bold
                  ),
                  modifier = Modifier
                    .clickable { mode = AuthMode.REGISTER }
                    .testTag("switch_to_register_button")
                )
              }
            }

            AuthMode.REGISTER -> {
              Text(
                text = "Create Business Account",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
              )

              OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text("Full Name") },
                leadingIcon = { Icon(Icons.Outlined.Person, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
              )

              OutlinedTextField(
                value = businessName,
                onValueChange = { businessName = it },
                label = { Text("Business / Company Name") },
                leadingIcon = { Icon(Icons.Outlined.Business, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
              )

              OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Phone Number") },
                leadingIcon = { Icon(Icons.Outlined.Phone, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
              )

              OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Work Email") },
                leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
              )

              OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Set Password") },
                leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null) },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
              )

              Button(
                onClick = { mode = AuthMode.EMAIL_VERIFICATION },
                modifier = Modifier
                  .fillMaxWidth()
                  .height(48.dp)
                  .testTag("register_submit_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
              ) {
                Text("Create Account", fontWeight = FontWeight.Bold)
              }

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
              ) {
                Text(
                  "Already registered? Sign In",
                  style = MaterialTheme.typography.bodySmall.copy(
                    color = BrandPrimary,
                    fontWeight = FontWeight.Bold
                  ),
                  modifier = Modifier.clickable { mode = AuthMode.LOGIN }
                )
              }
            }

            AuthMode.EMAIL_VERIFICATION -> {
              Text(
                text = "Verify Your Email",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
              )
              Text(
                text = "We sent a 6-digit confirmation code to $email.",
                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
              )

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
              ) {
                repeat(6) { idx ->
                  Box(
                    modifier = Modifier
                      .size(42.dp)
                      .clip(RoundedCornerShape(8.dp))
                      .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                  ) {
                    Text(
                      text = "${(idx * 2 + 7) % 10}",
                      fontWeight = FontWeight.Bold,
                      fontSize = 18.sp
                    )
                  }
                }
              }

              Button(
                onClick = { onAuthenticated() },
                modifier = Modifier
                  .fillMaxWidth()
                  .height(48.dp)
                  .testTag("verify_email_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
              ) {
                Text("Verify & Open Dashboard", fontWeight = FontWeight.Bold)
              }

              TextButton(
                onClick = { mode = AuthMode.LOGIN },
                modifier = Modifier.fillMaxWidth()
              ) {
                Text("Back to Sign In")
              }
            }

            AuthMode.FORGOT_PASSWORD -> {
              Text(
                text = "Reset Password",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
              )
              Text(
                text = "Enter your registered email and we'll send a password recovery link.",
                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
              )

              OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Registered Email") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
              )

              if (statusMessage != null) {
                Text(
                  text = statusMessage ?: "",
                  color = BrandPrimary,
                  style = MaterialTheme.typography.bodySmall
                )
              }

              Button(
                onClick = {
                  statusMessage = "Reset link sent to $email! Check inbox."
                },
                modifier = Modifier
                  .fillMaxWidth()
                  .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
              ) {
                Text("Send Reset Link")
              }

              TextButton(
                onClick = { mode = AuthMode.LOGIN },
                modifier = Modifier.fillMaxWidth()
              ) {
                Text("Return to Login")
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      Text(
        text = "Powered by Praneeth Kumar • Owner Phone: 8951858777",
        style = MaterialTheme.typography.labelSmall.copy(
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          fontWeight = FontWeight.Medium
        )
      )
    }
  }
}
