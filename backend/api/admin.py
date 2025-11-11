from django.contrib import admin
from .models import Apartment, Review, Profile

# Register your models here so they appear in the admin interface
admin.site.register(Apartment)
admin.site.register(Review)
admin.site.register(Profile)
