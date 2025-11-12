from rest_framework.decorators import api_view, permission_classes
from rest_framework.permissions import AllowAny, IsAuthenticated
from rest_framework.response import Response
from rest_framework import status
from django.contrib.auth import authenticate
from rest_framework.authtoken.models import Token
from django.contrib.auth.models import User
from .serializers import RegisterSerializer
from .models import Apartment, Review, Profile
from .serializers import ApartmentSerializer, ReviewSerializer, ProfileSerializer


#first test 
@api_view(['GET'])
@permission_classes([AllowAny])
def ping(request):
    return Response({"message": "pong!"})


#auth endpoints
@api_view(['POST'])
@permission_classes([AllowAny])
def register(request):
    """Register a new user and return an auth token"""
    serializer = RegisterSerializer(data=request.data)
    if serializer.is_valid():
        user = serializer.save()
        token, _ = Token.objects.get_or_create(user=user)
        return Response({'token': token.key}, status=201)
    return Response(serializer.errors, status=400)


@api_view(['POST'])
@permission_classes([AllowAny])
def obtain_token(request):
    """Login endpoint: validates user and returns token"""
    username = request.data.get('username')
    password = request.data.get('password')
    user = authenticate(username=username, password=password)
    if not user:
        return Response({'detail': 'Invalid credentials'}, status=400)
    token, _ = Token.objects.get_or_create(user=user)
    return Response({'token': token.key})


#apt endpoints
@api_view(['GET', 'POST'])
def apartments(request):
    if request.method == 'GET':
        qs = Apartment.objects.all().order_by('-created_at')
        return Response(ApartmentSerializer(qs, many=True).data)

    # POST (auth required)
    if not request.user.is_authenticated:
        return Response({'detail': 'Auth required'}, status=401)
    ser = ApartmentSerializer(data=request.data)
    if ser.is_valid():
        ser.save()
        return Response(ser.data, status=201)
    return Response(ser.errors, status=400)


@api_view(['GET'])
def apartment_detail(request, place_id):
    try:
        apt = Apartment.objects.get(google_place_id=place_id)

    except Apartment.DoesNotExist:
        return Response({'detail': 'Not found'}, status=404)
    return Response(ApartmentSerializer(apt).data)


# reviews
@api_view(['GET', 'POST'])
def apartment_reviews(request, place_id):

    apt, _ = Apartment.objects.get_or_create(
        google_place_id=place_id,
        defaults={"name": place_id, "address": "Unknown Address"}
    )

    # get review
    if request.method == 'GET':
        qs = apt.reviews.select_related('user').order_by('-created_at')
        return Response(ReviewSerializer(qs, many=True).data)

    # post
    if not request.user.is_authenticated:
        return Response({'detail': 'Auth required'}, status=401)

    data = request.data.copy()
    data['apartment'] = place_id  # ensure serializer knows which apartment

    ser = ReviewSerializer(data=data)
    if ser.is_valid():
        ser.save(user=request.user)
        return Response(ser.data, status=201)
    return Response(ser.errors, status=400)


# profile
@api_view(['GET', 'PUT'])
@permission_classes([IsAuthenticated])
def profile_me(request):
    prof, _ = Profile.objects.get_or_create(user=request.user)
    if request.method == 'GET':
        return Response(ProfileSerializer(prof).data)
    ser = ProfileSerializer(prof, data=request.data, partial=True)
    if ser.is_valid():
        ser.save()
        return Response(ser.data)
    return Response(ser.errors, status=400)

