from django.db import models
from django.conf import settings
from django.contrib.auth.models import User # Import the User model

# Create your models here.

class Apartment(models.Model):
    """
    Stores a local cache of apartment data, uniquely identified by its Google Places ID.
    Now includes a Many-to-Many field for favorites.
    """
    google_place_id = models.CharField(max_length=255, unique=True, primary_key=True)
    name = models.CharField(max_length=255)
    address = models.CharField(max_length=500)
    created_at = models.DateTimeField(auto_now_add=True)

    # NEW: A Many-to-Many relationship to track which users have favorited this apartment.
    # The 'related_name' lets us easily access a user's favorite apartments (e.g., user.favorite_apartments.all()).
    # 'blank=True' means a user is not required to have favorites.
    favorited_by = models.ManyToManyField(settings.AUTH_USER_MODEL, related_name='favorite_apartments', blank=True)

    def __str__(self):
        return f"{self.name} ({self.google_place_id})"

class Review(models.Model):
    """
    Represents a review for a specific apartment, linked via the Apartment model.
    The rating field is now active.
    """
    apartment = models.ForeignKey(Apartment, on_delete=models.CASCADE, related_name='reviews')
    user = models.ForeignKey(settings.AUTH_USER_MODEL, on_delete=models.CASCADE)

    # A rating system from 1 to 5.
    # rating = models.IntegerField(choices=[(i, i) for i in range(1, 6)])

    comment = models.TextField(blank=True)
    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)

    def __str__(self):
        return f'Review for {self.apartment.name} by {self.user.username} ({self.rating} stars)'

# NEW: A model to store extra information about a user.
class Profile(models.Model):
    """
    Extends Django's built-in User model to store profile-specific information.
    This is triggered by the presence of a SettingsActivity.
    """
    # A one-to-one link to the User model. If a User is deleted, their Profile is deleted too.
    user = models.OneToOneField(settings.AUTH_USER_MODEL, on_delete=models.CASCADE)

    # EXAMPLE FIELD: You can store a URL to a user's profile picture.
    profile_picture_url = models.URLField(max_length=500, blank=True, null=True)

    # EXAMPLE FIELD: Store a user's bio or short description.
    bio = models.TextField(blank=True)

    def __str__(self):
        return f'{self.user.username} Profile'

