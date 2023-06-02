import json
import boto3

dynamodb = boto3.resource('dynamodb')
table = dynamodb.Table('users')

def lambda_handler(event, context):
    # Verificar si se proporciona la carga útil en el cuerpo de la solicitud
    if not event:
        return {
            'statusCode': 400,
            'body': json.dumps({'error': 'Message data not provided in request body'})
        }

    # Extraer los campos individuales de la carga útil
    user_id = event['user_id']
    user_name = event['userName']
    email = event['email']
    status = event['status']

    # Almacenar el usuario en la tabla DynamoDB
    table.put_item(Item={
        'user_id': user_id,
        'userName': user_name,
        'email': email,
        'status': status
    })

    return {
        'statusCode': 200,
        'body': json.dumps({'message': 'User created successfully'})
    }
