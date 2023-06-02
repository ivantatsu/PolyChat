import json
import boto3
from decimal import Decimal

def lambda_handler(event, context):
    dynamodb = boto3.resource('dynamodb')
    table = dynamodb.Table('messages')

    body = json.loads(event["application/json"])
    message_id = body['message_id']

    response = table.get_item(Key={'message_id': message_id})
    if 'Item' not in response:
        return {
            'statusCode': 400,
            'body': json.dumps({'error': message_id + ' not in table'})
        }

    item = response['Item']

    # Convertir los sets a listas y Decimals a float
    for key, value in item.items():
        if isinstance(value, set):
            item[key] = list(value)
        if isinstance(value, Decimal):
            item[key] = float(value)

    # Ahora todos los tipos deben ser serializables
    return {
        'statusCode': 200,
        'body': json.dumps(item)
    }
