from django.shortcuts import render
from django.http import HttpResponse, JsonResponse
from django.views.decorators.csrf import csrf_exempt
from rest_framework.renderers import JSONRenderer
from rest_framework.parsers import JSONParser

# Create your views here.

def hello(request, name):
    """
    Retrieve, update or delete a code snippet.
    """
    if request.method == 'GET':
        return JsonResponse('Hello ! Your name is ' + name, safe=False)

    elif request.method == 'PUT':
        return JsonResponse('Not supported yet', status=404, safe=False)

    elif request.method == 'DELETE':
        snippet.delete()
        return JsonResponse('Not supported yet', status=404, safe=False)

def hello2(request, surname):
    """
    Retrieve, update or delete a code snippet.
    """
    if request.method == 'GET':
        return JsonResponse('Hello ! Your surname is ' + surname, safe=False)

    elif request.method == 'PUT':
        return JsonResponse('Not supported yet', status=404, safe=False)

    elif request.method == 'DELETE':
        snippet.delete()
        return JsonResponse('Not supported yet', status=404, safe=False)
