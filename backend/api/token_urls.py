from django.urls import path
from .views import obtain_token, register

urlpatterns = [
    path('login/', obtain_token, name='token-login'),
    path('register/', register, name='register'),
]
